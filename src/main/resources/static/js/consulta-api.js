(() => {
  const form = document.querySelector('#consulta'), select = document.querySelector('#categoria-api');
  const status = document.querySelector('#estado-api'), body = document.querySelector('#resultados-api');
  let version = 0;
  async function get(route) {
    const response = await fetch(form.dataset.api + route);
    if (!response.ok) throw new Error('No se pudo consultar el catálogo. Intenta de nuevo.');
    return response.json();
  }
  async function cargar() {
    const actual = ++version;
    body.replaceChildren(); status.textContent = 'Consultando…';
    try {
      const espacios = await get('espacios' + (select.value ? '?categoriaId=' + encodeURIComponent(select.value) : ''));
      if (actual !== version) return;
      for (const espacio of espacios) {
        const row = document.createElement('tr');
        for (const value of [espacio.nombre, espacio.distrito,
          new Intl.NumberFormat('es-PE', { style: 'currency', currency: espacio.moneda }).format(espacio.precioPorHora),
          espacio.capacidad, espacio.estado]) {
          const cell = document.createElement('td'); cell.textContent = value; row.append(cell);
        }
        body.append(row);
      }
      status.textContent = espacios.length ? espacios.length + ' espacios encontrados.' : 'No hay espacios en esta categoría.';
    } catch (error) { if (actual === version) status.textContent = error.message; }
  }
  form.addEventListener('submit', event => { event.preventDefault(); cargar(); });
  (async () => {
    try {
      for (const categoria of await get('categorias')) select.add(new Option(categoria.nombre, categoria.id));
      await cargar();
    } catch (error) { status.textContent = error.message; }
  })();
})();
