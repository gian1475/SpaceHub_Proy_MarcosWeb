// 1. buscador de espacios en el catalogo
function buscarLocal() {
  let texto = document.getElementById("inputBuscar").value.toLowerCase();
  let tarjetas = document.querySelectorAll(".col-12.col-md-6.col-lg-4");
  tarjetas.forEach(function (col) {
    let contenido = col.innerText.toLowerCase();
    col.style.display = contenido.includes(texto) ? "block" : "none";
  });
}

// 2. calculadora de precio en vivo para el modal
function calcularTotal() {
  let formReserva = document.querySelector("#modalReserva form");
  let precioHora = 45;

  if (formReserva && formReserva.dataset.precio) {
    precioHora = parseFloat(formReserva.dataset.precio);
  } else {
    let elemPrecio = document.getElementById("precioServicio");
    if (elemPrecio && elemPrecio.dataset.precio) {
      precioHora = parseFloat(elemPrecio.dataset.precio);
    }
  }

  let horaInElem = document.getElementById("horaInicio");
  let horaOutElem = document.getElementById("horaFin");
  if (!horaInElem || !horaOutElem) return;

  let horaIn = parseInt(horaInElem.value.split(":")[0]);
  let horaOut = parseInt(horaOutElem.value.split(":")[0]);
  let totalHoras = horaOut - horaIn;

  let textoHorasElem = document.getElementById("textoHoras");
  let totalPagarElem = document.getElementById("totalPagar");

  if (totalHoras > 0) {
    let monto = totalHoras * precioHora;
    if (textoHorasElem) textoHorasElem.innerText = totalHoras + " hora(s)";
    if (totalPagarElem) totalPagarElem.innerText = "S/ " + monto.toFixed(2);
  } else {
    if (textoHorasElem) textoHorasElem.innerText = "1 hora(s)";
    if (totalPagarElem) totalPagarElem.innerText = "S/ " + precioHora.toFixed(2);
  }
}

// Ejecutar al cargar la página si existen los elementos
document.addEventListener("DOMContentLoaded", function () {
  if (document.getElementById("horaInicio") && document.getElementById("horaFin")) {
    calcularTotal();
  }
});
