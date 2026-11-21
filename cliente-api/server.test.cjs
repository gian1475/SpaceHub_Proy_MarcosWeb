const { test } = require('node:test');
const assert = require('node:assert/strict');
const http = require('node:http');
test('cliente independiente sirve la página, consulta Spring y maneja su caída', async () => {
  const backend = http.createServer(async (req,res) => {
    if (req.url === '/api/auth/login') {
      assert.equal(req.method,'POST');
      assert.equal(req.headers['content-type'],'application/json');
      let body=''; for await (const chunk of req) body+=chunk;
      assert.equal(JSON.parse(body).correo,'prueba@example.com');
      res.setHeader('Content-Type','application/json');
      return res.end(JSON.stringify({access_token:'token-de-prueba',token_type:'Bearer',expires_in:3600}));
    }
    if (req.headers.authorization !== 'Bearer token-de-prueba') {
      res.writeHead(401,{'Content-Type':'application/json'}); return res.end('{"mensaje":"Token requerido"}');
    }
    if (req.url === '/api/v1/interrumpida') {
      res.writeHead(200, { 'Content-Type': 'application/json', 'Content-Length': '1000' });
      res.write('{');
      setTimeout(() => res.destroy(), 20);
      return;
    }
    assert.equal(req.url, '/api/v1/espacios?categoriaId=3');
    res.setHeader('Content-Type','application/json');
    res.end(JSON.stringify([{ id: 7, nombre: 'Sala', moneda: 'PEN' }]));
  });
  await new Promise(r => backend.listen(0,'127.0.0.1',r));
  process.env.SPACEHUB_API_URL = 'http://127.0.0.1:' + backend.address().port;
  const server = require('./server.cjs');
  await new Promise(r => server.listen(0,'127.0.0.1',r));
  const base = 'http://127.0.0.1:' + server.address().port;
  try {
    assert.match(await (await fetch(base)).text(), /SpaceHub/);
    assert.equal((await fetch(base+'/api/v1/espacios?categoriaId=3')).status,401);
    const login = await fetch(base+'/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({correo:'prueba@example.com',password:'clave-prueba'})});
    assert.equal(login.status,200);
    assert.equal((await login.json()).access_token,'token-de-prueba');
    const headers = {Authorization:'Bearer token-de-prueba'};
    const result = await fetch(base+'/api/v1/espacios?categoriaId=3',{headers});
    assert.equal(result.status,200);
    assert.equal((await result.json())[0].id,7);
    assert.equal((await fetch(base+'/secreto')).status,404);
    assert.equal((await fetch(base,{method:'POST'})).status,405);
    const malformed = await new Promise((resolve, reject) => {
      http.get({ hostname:'127.0.0.1', port:server.address().port, path:'//[' }, response => {
        response.resume(); resolve(response.statusCode);
      }).on('error', reject);
    });
    assert.equal(malformed,400);
    const interrupted = await fetch(base+'/api/v1/interrumpida',{headers});
    assert.equal(interrupted.status,502);
    assert.match((await interrupted.json()).mensaje,/No se pudo conectar/);
    assert.equal((await fetch(base)).status,200);
    await new Promise(r => backend.close(r));
    assert.equal((await fetch(base+'/api/v1/espacios')).status,502);
  } finally {
    await new Promise(r => server.close(r));
    if(backend.listening) await new Promise(r => backend.close(r));
  }
});
