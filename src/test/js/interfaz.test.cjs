const {test} = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const vm = require("node:vm");
const path = require("node:path");
const recursos = path.resolve(__dirname, "../../main/resources/static/js");

test("la búsqueda ignora tildes y avisa cuando no hay coincidencias", () => {
  const entrada = {value: "reunion"};
  const aviso = {hidden: true};
  const tarjetas = [{textContent: "Sala de reunión", hidden: false}, {textContent: "Terraza", hidden: false}];
  const contexto = {
    document: {
      getElementById: id => ({inputBuscar: entrada, sinCoincidencias: aviso})[id],
      querySelectorAll: () => tarjetas,
      addEventListener() {}
    }
  };
  vm.createContext(contexto);
  vm.runInContext(fs.readFileSync(path.join(recursos, "script.js"), "utf8"), contexto);
  contexto.buscarLocal();
  assert.equal(tarjetas[0].hidden, false);
  assert.equal(tarjetas[1].hidden, true);
  assert.equal(aviso.hidden, true);
  entrada.value = "cine";
  contexto.buscarLocal();
  assert.equal(aviso.hidden, false);
  entrada.value = "";
  contexto.buscarLocal();
  assert.ok(tarjetas.every(t => !t.hidden));
  assert.equal(aviso.hidden, true);
});

test("la calculadora usa la tarifa real y rechaza reservas menores de una hora", () => {
  let validacion = "";
  const campos = {
    horaInicio: {value: "10:00"},
    horaFin: {value: "11:30", setCustomValidity: texto => validacion = texto},
    textoHoras: {},
    totalPagar: {}
  };
  const formulario = {dataset: {precio: "45.50"}};
  const contexto = {document: {
    querySelector: () => formulario,
    getElementById: id => campos[id],
    addEventListener() {}
  }};
  vm.createContext(contexto);
  vm.runInContext(fs.readFileSync(path.join(recursos, "script.js"), "utf8"), contexto);
  contexto.calcularTotal();
  assert.equal(campos.totalPagar.textContent, "S/ 68.25");
  assert.equal(validacion, "");
  campos.horaFin.value = "10:59";
  contexto.calcularTotal();
  assert.notEqual(validacion, "");
  assert.equal(campos.totalPagar.textContent, "—");
  campos.horaFin.value = "11:00";
  delete formulario.dataset.precio;
  contexto.calcularTotal();
  assert.equal(campos.totalPagar.textContent, "—");
  formulario.dataset.precio = "45.50";
  campos.horaFin.value = "09:00";
  contexto.calcularTotal();
  assert.notEqual(validacion, "");
});

test("cambiar de categoría descarta el espacio anterior antes de consultar", () => {
  let cambiar;
  let envios = 0;
  const espacio = {value: "42", disabled: false};
  const categoria = {addEventListener: (evento, fn) => { assert.equal(evento, "change"); cambiar = fn; }};
  const formulario = {
    querySelector: selector => selector === "[data-categoria]" ? categoria : espacio,
    requestSubmit: () => envios++
  };
  vm.runInNewContext(fs.readFileSync(path.join(recursos, "filtros-espacios.js"), "utf8"),
    {document: {querySelectorAll: () => [formulario]}});
  cambiar();
  assert.equal(espacio.value, "");
  assert.equal(espacio.disabled, true);
  assert.equal(envios, 1);
});
