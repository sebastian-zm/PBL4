let notiCount = 0;

fetch('/api/notifications/unread')
  .then(response => response.json())
  .then(notifications => {
    notifications.forEach(notification => {
      showNotification(notification.message, notification.id); // <- importante: pasar también ID
    });
  })
  .catch(error => console.error('Error al cargar notificaciones no leídas:', error));

fetch('/api/notifications/usuarioId')
  .then(response => response.json())
  .then(usuarioId => {
    console.log('UsuarioId recibido:', usuarioId);

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
      stompClient.subscribe('/user/queue/notificaciones', (msg) => {
        const data = JSON.parse(msg.body); // Asegúrate de que envías JSON desde el backend
        showNotification(data.message, data.id); // <- pasar tanto message como id
      });
    }, (error) => {
      console.error('Error al conectar WebSocket:', error);
    });
  })
  .catch(error => {
    console.error('Error al obtener el usuarioId:', error);
  });

function quitarNotificacion(button, notificationId) {
  const li = button.closest('li');
  if (li) li.remove();

  if (notificationId) {
    fetch(`/api/notifications/read/${notificationId}`, { method: 'POST' });
  }

  notiCount = Math.max(0, notiCount - 1);
  updateNotiCounter();
}

function showNotification(content, notificationId = null) {
  const notiList = document.getElementById('notiList');

  const placeholder = document.querySelector('#notiList .dropdown-item-text');
  if (placeholder) placeholder.remove();

  const li = document.createElement('li');
  li.innerHTML = `
    <span class="dropdown-item d-flex justify-content-between align-items-center">
      ${content}
      <button class="btn btn-sm btn-success ms-2" onclick="quitarNotificacion(this, ${notificationId})">✔</button>
    </span>
  `;

  notiList.prepend(li);
  notiCount++;
  updateNotiCounter();
}

function updateNotiCounter() {
  const countElement = document.getElementById('notiCount');

  if (notiCount > 0) {
    countElement.textContent = notiCount;
    countElement.classList.remove('d-none');
    document.getElementById('notiBell').classList.add('shake');
  } else {
    notiList.textContent = ' No tienes notificaciones';
    countElement.classList.add('d-none');
    document.getElementById('notiBell').classList.remove('shake');
  }
}

// NO REINICIAR el contador al abrir el dropdown
document.getElementById('notiDropdown').addEventListener('click', () => {
  document.getElementById('notiBell').classList.remove('shake');
  // No tocar el contador aquí
});
