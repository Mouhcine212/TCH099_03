document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.getElementById('logoutBtn');
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

  const form = document.getElementById('searchForm');
  const resultsContainer = document.getElementById('results');

  if (form && resultsContainer) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const destination = document.getElementById('destination').value.trim();
      const date = document.getElementById('date').value;

      try {
        const res = await fetch('http://localhost/api/search_flights', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ destination, date })
        });

        const data = await res.json();
        console.log('Résultat reçu de l\'API :', data);

        if (Array.isArray(data) && data.length > 0) {
          resultsContainer.innerHTML = data.map(vol => `
            <div class="vol-item">
              <h3>${vol.DESTINATION}</h3>
              <p><strong>Origine :</strong> ${vol.ORIGINE}</p>
              <p><strong>Départ :</strong> ${vol.HEURE_DEPART}</p>
              <p><strong>Arrivée :</strong> ${vol.HEURE_ARRIVEE}</p>
              <p><strong>Compagnie :</strong> ${vol.COMPAGNIE}</p>
              <p><strong>Numéro de vol :</strong> ${vol.NUMERO_VOL}</p>
              <p><strong>Classe :</strong> ${vol.CLASSE}</p>
              <p><strong>Prix :</strong> ${vol.PRIX} $</p>
              <p><strong>Sièges disponibles :</strong> ${vol.SIEGES_DISPONIBLES}</p>
              <button onclick="window.location.href='confirmation.html?id=${vol.ID_VOL}'">Réserver</button>
            </div>
          `).join('');
        } else {
          resultsContainer.innerHTML = '<p class="info">Aucun vol trouvé.</p>';
        }
      } catch (error) {
        console.error('Erreur fetch:', error);
        resultsContainer.innerHTML = '<p class="error">Erreur de connexion au serveur.</p>';
      }
    });
  }
});
