document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.getElementById('logoutBtn');
  const container = document.getElementById('historyResults');
  const token = localStorage.getItem('token');

  if (!token) {
    window.location.replace('login.html');
    return;
  }

  window.history.pushState(null, '', window.location.href);
  window.addEventListener('popstate', () => {
    window.history.pushState(null, '', window.location.href);
  });

  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('token');
      window.location.replace('login.html');
    });
  }

  if (container) {
    fetch('http://localhost/api/historique', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    .then(res => res.json())
    .then(data => {
      if (Array.isArray(data) && data.length > 0) {
        container.innerHTML = data.map(resa => {
          const dateNaissance = resa.DATE_NAISSANCE ? `<p><strong>Date de naissance :</strong> ${resa.DATE_NAISSANCE}</p>` : '';
          const passeport = resa.NUMERO_PASSEPORT ? `<p><strong>Numéro de passeport :</strong> ${resa.NUMERO_PASSEPORT}</p>` : '';

          return `
            <div class="history-item">
              <p><strong>Vol :</strong> ${resa.ORIGINE} → ${resa.DESTINATION}</p>
              <p><strong>Départ :</strong> ${new Date(resa.HEURE_DEPART).toLocaleString()}</p>
              <p><strong>Arrivée :</strong> ${new Date(resa.HEURE_ARRIVEE).toLocaleString()}</p>
              <p><strong>Siège :</strong> ${resa.NUMERO_SIEGE}</p>
              <p><strong>Compagnie :</strong> ${resa.COMPAGNIE}</p>
              <p><strong>Prix :</strong> ${resa.PRIX} $</p>
              ${dateNaissance}
              ${passeport}
              <p><strong>Statut :</strong> ${resa.STATUT ?? "Confirmé"}</p>
              <hr/>
            </div>
          `;
        }).join('');
      } else {
        container.innerHTML = `<p class="info">Aucun vol réservé trouvé.</p>`;
      }
    })
    .catch(() => {
      container.innerHTML = '<p class="error">Erreur de connexion au serveur.</p>';
    });
  }
});
