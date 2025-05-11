let draggedId = null;

const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const csrfParam  = document.querySelector('meta[name="_csrf_parameterName"]').content;
const csrfToken  = document.querySelector('meta[name="_csrf"]').content;

function dragStart(e) {
  draggedId = e.currentTarget.dataset.id;
  if (draggedId) {
    e.stopPropagation();
  }
  e.dataTransfer.effectAllowed = 'move';
}

function dragOver(e) {
  e.preventDefault();
  e.dataTransfer.dropEffect = 'move';
}

function drop(e) {
  e.preventDefault();
  let targetId = e.currentTarget.dataset.id;
  if (targetId) {
    e.stopPropagation();
  }
  if (draggedId && draggedId !== targetId) {
    fetch(`/etiquetas/${draggedId}/move?parentId=${targetId}`, {
      method: 'POST',
      headers: { 
        [csrfHeader]: csrfToken 
      }
    }).then(r => {
      if (r.ok) location.reload();
    });
  }
}

// Mostrar/ocultar formularios
function showEditForm(btn) {
  let li = btn.closest('li');
  li.querySelector('.form-edit').classList.toggle('d-none');
}
function showAddChildForm(btn) {
  let li = btn.closest('li');
  li.querySelector('.form-add-child').classList.toggle('d-none');
}
function deleteNode(btn) {
  let id = btn.closest('li').dataset.id;
  if (confirm('¿Borrar etiqueta ' + id + ' y reparentar?')) {
    fetch(`/etiquetas/${id}/delete`, {
      method:'POST', 
      headers: { 
        [csrfHeader]: csrfToken 
      }
    })
      .then(r => r.redirected ? location.href = r.url : location.reload());
  }
}

// Creación de raíz
document.addEventListener('DOMContentLoaded', () => {

  document.getElementById('btnAddRoot')
    .addEventListener('click', () => {
      let name = document.getElementById('newName').value.trim();
      if (!name) return alert('Nombre vacío');
      let form = document.createElement('form');
      form.method = 'post';
      form.action = '/etiquetas';
      form.innerHTML = `
        <input type="hidden" name="${csrfParam}" value="${csrfToken}"/>
        <input name="nombre" value="${name}"/>
      `;
      document.body.appendChild(form);
      form.submit();
    });
});
