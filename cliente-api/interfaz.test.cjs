const {test}=require('node:test');
const assert=require('node:assert/strict');
const fs=require('node:fs');
const path=require('node:path');
const vm=require('node:vm');
const codigo=fs.readFileSync(path.join(__dirname,'index.html'),'utf8').match(/<script>([\s\S]*?)<\/script>/)[1];
const respuesta=(datos,status=200)=>({ok:status>=200&&status<300,status,json:async()=>datos});
function entorno(){
  function elemento(){return {value:'',children:[],handlers:{},replaceChildren(...hijos){this.children=hijos;},append(...hijos){this.children.push(...hijos);},add(hijo){this.children.push(hijo);},addEventListener(nombre,fn){this.handlers[nombre]=fn;},reset(){}};}
  const campos=Object.fromEntries(['categoria','estado','espacios','acceso','salir','actualizar','correo','clave'].map(id=>['#'+id,elemento()]));
  const boton=elemento();campos['#acceso'].querySelector=()=>boton;
  let token='primero', pendiente;
  const contexto={document:{querySelector:id=>campos[id],createElement:()=>elemento()},Option:function(text,value){return {text,value};},Intl,
    fetch:async(ruta,opciones)=>{
      if(pendiente)return pendiente(ruta,opciones);
      return ruta==='/api/auth/login'?respuesta({access_token:token}):respuesta([]);
    }};
  vm.createContext(contexto);vm.runInContext(codigo,contexto);
  return {campos,contexto,setFetch(fn){pendiente=fn;},
    async login(nuevo){token=nuevo;pendiente=null;await campos['#acceso'].handlers.submit({preventDefault(){}});}};
}
test('cerrar sesión descarta categorías pendientes y muestra la expiración',async()=>{
  const e=entorno();await e.login('primero');
  let resolver;e.setFetch(()=>new Promise(r=>resolver=r));
  const carga=e.contexto.iniciar();e.campos['#salir'].handlers.click();
  resolver(respuesta([{id:9,nombre:'Respuesta antigua'}]));await carga;
  assert.equal(e.campos['#categoria'].children.length,1);
  assert.equal(e.campos['#estado'].textContent,'Sesión del cliente cerrada.');
  await e.login('segundo');e.setFetch(async()=>respuesta({},401));await e.contexto.cargar();
  assert.match(e.campos['#estado'].textContent,/venció o fue revocado/);
  assert.equal(e.campos['#acceso'].hidden,false);
});
test('un 401 antiguo no cierra una sesión nueva',async()=>{
  const e=entorno();await e.login('primero');
  let resolver;e.setFetch(()=>new Promise(r=>resolver=r));const carga=e.contexto.cargar();
  e.campos['#salir'].handlers.click();await e.login('segundo');
  resolver(respuesta({},401));await carga;
  assert.equal(e.campos['#acceso'].hidden,true);
  assert.equal(e.campos['#categoria'].disabled,false);
  assert.equal(e.campos['#estado'].textContent,'No hay espacios en esta categoría.');
});
