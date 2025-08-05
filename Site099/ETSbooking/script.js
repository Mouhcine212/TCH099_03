const searchForm = document.getElementById('searchForm');
if (searchForm) {
  searchForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const destination = document.getElementById('destination').value.trim();
    const date = document.getElementById('date').value;

    const resultDiv = document.getElementById('results');
    resultDiv.innerHTML = '';

    try {
      const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/search_flights.php', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ destination, date })
      });

      const data = await res.json();

      if (res.ok && Array.isArray(data) && data.length > 0) {
        data.forEach(vol => {
          const card = document.createElement('div');
          card.classList.add('flight-card');
          card.innerHTML = `
            <h3>${vol.NUMERO_VOL} – ${vol.DESTINATION}</h3>
            <p>Compagnie : ${vol.COMPAGNIE}</p>
            <p>Classe : ${vol.CLASSE}</p>
            <p>Heure de départ : ${vol.HEURE_DEPART}</p>
            <p>Heure d'arrivée : ${vol.HEURE_ARRIVEE}</p>
            <p>Prix : ${vol.PRIX} $</p>
            <p>Sièges disponibles : ${vol.SIEGES_DISPONIBLES}</p>
            <button onclick="window.location.href='reservation.html?id=${vol.ID_VOL}'">
              Réserver
            </button>
          `;
          resultDiv.appendChild(card);
        });
      } else {
        resultDiv.innerHTML = '<p>Aucun vol trouvé.</p>';
      }
    } catch (err) {
      resultDiv.innerHTML = '<p>Erreur serveur. Réessayez plus tard.</p>';
    }
  });
}
