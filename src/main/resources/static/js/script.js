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

  const minutos = (valor) => {
    if (!/^\d{2}:\d{2}$/.test(valor)) return NaN;
    const [horas, minutos] = valor.split(":").map(Number);
    return horas * 60 + minutos;
  };
  const duracion = minutos(horaOutElem.value) - minutos(horaInElem.value);
  const totalHoras = duracion / 60;

  let textoHorasElem = document.getElementById("textoHoras");
  let totalPagarElem = document.getElementById("totalPagar");

  if (totalHoras > 0) {
    let monto = totalHoras * precioHora;
    if (textoHorasElem) textoHorasElem.innerText = Math.floor(duracion / 60) + " h " + (duracion % 60) + " min";
    if (totalPagarElem) totalPagarElem.innerText = "S/ " + monto.toFixed(2);
  } else {
    if (textoHorasElem) textoHorasElem.innerText = "Horario inv\u00e1lido";
    if (totalPagarElem) totalPagarElem.innerText = "S/ 0.00";
  }
}

// Ejecutar al cargar la página si existen los elementos
document.addEventListener("DOMContentLoaded", function () {
  if (document.getElementById("horaInicio") && document.getElementById("horaFin")) {
    calcularTotal();
  }
});
