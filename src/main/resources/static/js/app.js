// src/main/resources/static/js/app.js
document.addEventListener('DOMContentLoaded', () => {
  // Sólo un console.log de prueba para confirmar que carga
  console.log('app.js cargado correctamente 🎉');

  // Aquí puedes pegar todo el código de toggler, tooltips, shake, etc.
  // Por ejemplo, para modo claro/oscuro:
  const html = document.documentElement;
  const btn  = document.getElementById('theme-toggle');
  if (html && btn) {
    const setTheme = theme => {
      html.dataset.theme = theme;
      sessionStorage.setItem('theme', theme);
      btn.innerHTML = theme === 'dark' ? '<i class="fas fa-sun"></i>' : '<i class="fas fa-moon"></i>';
    };
    const saved = sessionStorage.getItem('theme');
    if (saved) setTheme(saved);
    else if (window.matchMedia('(prefers-color-scheme: dark)').matches) setTheme('dark');
    btn.addEventListener('click', () => {
      setTheme(html.dataset.theme === 'dark' ? 'light' : 'dark');
    });
  }
});
