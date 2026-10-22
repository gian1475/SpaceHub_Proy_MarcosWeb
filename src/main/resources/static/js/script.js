// Buscamos solo en las tarjetas del catálogo y avisamos si ninguna coincide.
function buscarLocal() {
  const entrada = document.getElementById("inputBuscar");
  if (!entrada) return;
  const normalizar = valor => valor.normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
  const texto = normalizar(entrada.value);
  const tarjetas = document.querySelectorAll("[data-espacio-tarjeta]");
  let visibles = 0;
  tarjetas.forEach(tarjeta => {
    tarjeta.hidden = !normalizar(tarjeta.textContent).includes(texto);
    if (!tarjeta.hidden) visibles++;
  });
  const aviso = document.getElementById("sinCoincidencias");
  if (aviso) aviso.hidden = tarjetas.length === 0 || visibles > 0;
}

// La tarifa viene del espacio abierto; el servidor confirma el importe al guardar.
function calcularTotal() {
  const formulario = document.querySelector("#modalReserva form");
  const precio = document.getElementById("precioServicio");
  const tarifaTexto = formulario?.dataset.precio ?? precio?.dataset.precio;
  const tarifa = tarifaTexto ? Number(tarifaTexto) : NaN;
  const inicio = document.getElementById("horaInicio");
  const fin = document.getElementById("horaFin");
  if (!inicio || !fin) return;
  const minutos = valor => {
    if (!/^\d{2}:\d{2}$/.test(valor)) return NaN;
    const [hora, minuto] = valor.split(":").map(Number);
    return hora < 24 && minuto < 60 ? hora * 60 + minuto : NaN;
  };
  const duracion = minutos(fin.value) - minutos(inicio.value);
  const horarioValido = Number.isFinite(duracion) && duracion >= 60;
  fin.setCustomValidity(horarioValido ? "" : "La reserva debe durar al menos una hora y terminar el mismo día.");
  const textoHoras = document.getElementById("textoHoras");
  const total = document.getElementById("totalPagar");
  if (textoHoras) textoHoras.textContent = horarioValido
    ? Math.floor(duracion / 60) + " h " + (duracion % 60) + " min"
    : "Mínimo 1 hora";
  if (total) {
    // Trabajamos con céntimos para que el redondeo coincida con el cálculo del servidor.
    const centimos = Number.isFinite(tarifa) && tarifa >= 1
      ? Math.round(Math.round(tarifa * 100) * duracion / 60) : NaN;
    total.textContent = horarioValido && Number.isFinite(centimos)
      ? "S/ " + (centimos / 100).toFixed(2) : "—";
  }
}

// Mostramos la estimación inicial cuando el formulario está listo.
document.addEventListener("DOMContentLoaded", () => {
  calcularTotal();
  buscarLocal();
});
