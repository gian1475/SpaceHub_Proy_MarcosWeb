function buscarLocal() {
  let texto = document.getElementById("inputBuscar").value.toLowerCase();
  let tarjetas = document.querySelectorAll(".col-12.col-md-6.col-lg-4");
  tarjetas.forEach(function (col) {
    let contenido = col.innerText.toLowerCase();
    col.style.display = contenido.includes(texto) ? "block" : "none";
  });
}

function calcularTotal() {
  let horaIn = parseInt(document.getElementById("horaInicio").value.split(":")[0]);
  let horaOut = parseInt(document.getElementById("horaFin").value.split(":")[0]);
  let precioHora = 45;
  let totalHoras = horaOut - horaIn;

  if (totalHoras > 0) {
    let monto = totalHoras * precioHora;
    document.getElementById("textoHoras").innerText = totalHoras + " hora(s)";
    document.getElementById("totalPagar").innerText = "S/ " + monto + ".00";
  } else {
    document.getElementById("textoHoras").innerText = "1 hora(s)";
    document.getElementById("totalPagar").innerText = "S/ 45.00";
  }
}

function enviarReserva(event) {
  event.preventDefault();
  let nombre = document.getElementById("nombreCliente").value;
  let fecha = document.getElementById("fechaReserva").value;
  let total = document.getElementById("totalPagar").innerText;

  alert("¡Gracias " + nombre + "! Tu solicitud de reserva para el " + fecha + " por un total estimado de " + total + " fue enviada exitosamente.");

  let modalElemento = document.getElementById("modalReserva");
  let modalBootstrap = bootstrap.Modal.getInstance(modalElemento);
  if (modalBootstrap) {
    modalBootstrap.hide();
  }
}
