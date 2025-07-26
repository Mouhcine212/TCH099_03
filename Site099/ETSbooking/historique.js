document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.getElementById('logoutBtn');
  const container = document.getElementById('historique'); // ID correct de ta div HTML
  const token = localStorage.getItem('token');

  //  Redirige vers login si pas connecté
  if (!token) {
    window.location.replace('login.html');
    return;
  }

  //  Bloque le bouton retour après déconnexion
  window.history.pushState(null, '', window.location.href);
  window.addEventListener('popstate', () => {
    window.history.pushState(null, '', window.location.href);
  });

  //  Gère le bouton déconnexion
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('token');
      window.location.replace('login.html');
    });
  }

  //  Récupère et affiche l’historique des vols
  if (container) {
    fetch('http://localhost/api/get_flight_history', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => res.json())
    .then(data => {
      if (Array.isArray(data) && data.length > 0) {
        container.innerHTML = data.map(flight => `
          <div class="history-item">
            <p><strong>${flight.DESTINATION}</strong> – ${flight.DATE}</p>
          </div>
        `).join('');
      } else {
        container.innerHTML = `<p class="info">Aucun vol réservé trouvé.</p>`;
      }
    })
    .catch(() => {
      container.innerHTML = '<p class="error">Erreur de connexion au serveur.</p>';
    });
  }
});
