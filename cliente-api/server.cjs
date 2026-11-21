// Cliente independiente de Spring Boot, sin dependencias npm.
const http = require('node:http');
const fs = require('node:fs/promises');
const path = require('node:path');
const backend = process.env.SPACEHUB_API_URL || 'http://localhost:8080';
const server = http.createServer(async (req, res) => {
  let url;
  try { url = new URL(req.url, 'http://localhost'); }
  catch { res.writeHead(400); return res.end('Solicitud inválida.'); }
  const login = url.pathname === '/api/auth/login';
  if (req.method !== 'GET' && !(login && req.method === 'POST')) { res.writeHead(405, { Allow: 'GET' }); return res.end(); }
  if (login || url.pathname.startsWith('/api/v1/')) {
    try {
      let payload;
      if (login && req.method === 'POST') {
        const chunks = []; let size = 0;
        for await (const chunk of req) {
          size += chunk.length;
          if (size > 8192) { res.writeHead(413); return res.end(); }
          chunks.push(chunk);
        }
        payload = Buffer.concat(chunks);
      }
      const upstream = await fetch(new URL(url.pathname + url.search, backend), {
        method: req.method, body: payload,
        signal: AbortSignal.timeout(5000), headers: { Accept: 'application/json', 'Content-Type': 'application/json',
          ...(req.headers.authorization ? { Authorization: req.headers.authorization } : {}) }, redirect: 'error'
      });
      // Leer antes de enviar cabeceras permite devolver 502 si el backend corta el cuerpo.
      const body = await upstream.text();
      res.writeHead(upstream.status, { 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' });
      return res.end(body);
    } catch {
      res.writeHead(502, { 'Content-Type': 'application/json' });
      return res.end(JSON.stringify({ mensaje: 'No se pudo conectar con SpaceHub. Comprueba que Spring Boot esté iniciado.' }));
    }
  }
  if (url.pathname !== '/') { res.writeHead(404); return res.end(); }
  try {
    const page = await fs.readFile(path.join(__dirname, 'index.html'));
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(page);
  } catch {
    res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('No se pudo cargar la página.');
  }
});
if (require.main === module) {
  let port = 5500;
  server.on('error', error => {
    if (error.code === 'EADDRINUSE' && port < 5510) {
      console.log(`El puerto ${port} está ocupado; probando ${port + 1}…`);
      server.listen(++port, '127.0.0.1');
    } else {
      console.error('No se pudo iniciar el cliente:', error.message);
      process.exitCode = 1;
    }
  });
  server.on('listening', () => console.log(`Cliente API: http://127.0.0.1:${port}\nMantén esta terminal abierta. Spring Boot debe estar iniciado en el puerto 8080.`));
  server.listen(port, '127.0.0.1');
}
module.exports = server;
