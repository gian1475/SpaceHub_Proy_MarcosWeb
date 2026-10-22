// Al cambiar de categoría, quitamos el espacio anterior para no mezclar filtros.
document.querySelectorAll('[data-filtros-espacios]').forEach(form => {
    const categoria = form.querySelector('[data-categoria]');
    const espacio = form.querySelector('[data-espacio]');
    categoria.addEventListener('change', () => {
        espacio.value = '';
        espacio.disabled = true;
        form.requestSubmit();
    });
});
