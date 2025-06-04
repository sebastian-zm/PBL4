const hiloId = document.body.dataset.hiloId;
const usuarioActual = document.body.dataset.usuario;

// Establecer conexión con WebSocket del chat
const socket = new SockJS('/chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/mensajes', function (msg) {
        const data = JSON.parse(msg.body);

        if (data.hiloId == hiloId) {
            const box = document.getElementById('chat-box');

            const contenedor = document.createElement('div');
            contenedor.className = data.usuarioNombre === usuarioActual ? 'text-end' : 'text-start';

            const burbuja = document.createElement('div');
            burbuja.className = 'd-inline-block px-3 py-2 rounded mb-2 ' +
                (data.usuarioNombre === usuarioActual ? 'bg-primary text-white' : 'bg-white border');

            burbuja.innerHTML = `
                <div><strong>${data.usuarioNombre}</strong></div>
                <div>${data.contenido}</div>
                <div class="small text-muted">${new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</div>
            `;

            contenedor.appendChild(burbuja);
            box.appendChild(contenedor);

            // Auto-scroll
            box.scrollTop = box.scrollHeight;
        }
    });
});

// Enviar mensaje
function enviar() {
    const input = document.getElementById('mensaje');
    const contenido = input.value.trim();

    if (contenido !== '') {
        stompClient.send("/app/mensaje", {}, JSON.stringify({
            hiloId: hiloId,
            contenido: contenido
        }));
        input.value = '';
    }
}
