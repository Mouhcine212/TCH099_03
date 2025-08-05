document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('searchForm');
  const resultsCards = document.getElementById('resultsCards');
  const loader = document.getElementById('loader');
  const origineList = document.getElementById('origines');
  const destinationList = document.getElementById('destinations');
  const token = localStorage.getItem('token');

  if (!token) {
    window.location.href = 'login.html';
    return;
  }

  fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/get_airports', {
    headers: { 'Authorization': `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      origineList.innerHTML = '';
      destinationList.innerHTML = '';
      data.forEach(aeroport => {
        const option = `${aeroport.VILLE} (${aeroport.CODE_IATA})`;
        const opt1 = document.createElement('option');
        opt1.value = option;
        origineList.appendChild(opt1);

        const opt2 = document.createElement('option');
        opt2.value = option;
        destinationList.appendChild(opt2);
      });
    })
    .catch(err => console.error('Erreur chargement aéroports :', err));

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    resultsCards.innerHTML = '';
    loader.style.display = 'flex';

    const origine = document.getElementById('origine').value.trim();
    const destination = document.getElementById('destination').value.trim();
    const dateDepart = document.getElementById('dateDepart').value.trim();

    const body = { origine, destination };
    if (dateDepart) body.dateDepart = dateDepart;

    try {
      const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/search_flights.php', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(body)
      });

      const vols = await res.json();
      loader.style.display = 'none';
      resultsCards.innerHTML = '';

      if (!Array.isArray(vols) || vols.length === 0) {
        resultsCards.innerHTML =
          '<p style="text-align:center;color:#666;">Aucun vol trouvé pour cette recherche.</p>';
        return;
      }

      vols.forEach(vol => {
        const card = document.createElement('div');
        card.className = 'flight-card';

        const depart = new Date(vol.HEURE_DEPART);
        const arrivee = new Date(vol.HEURE_ARRIVEE);
        const dureeHeures = Math.floor((arrivee - depart) / 1000 / 60 / 60);
        const dureeMinutes = Math.floor(((arrivee - depart) / 1000 / 60) % 60);

        card.innerHTML = `
          <h3>${vol.COMPAGNIE} - ${vol.NUMERO_VOL}</h3>
          <div class="flight-info">
            <span>${vol.ORIGINE_VILLE} (${vol.ORIGINE_CODE})</span>
            <span>→</span>
            <span>${vol.DESTINATION_VILLE} (${vol.DESTINATION_CODE})</span>
          </div>
          <div class="flight-info">
            <span>Départ : ${depart.toLocaleString()}</span>
            <span>Arrivée : ${arrivee.toLocaleString()}</span>
          </div>
          <div class="flight-info" style="font-style:italic;color:#777;">
            Durée estimée : ${dureeHeures}h ${dureeMinutes}m
          </div>
          <div class="flight-price">${vol.PRIX} $ - ${vol.CLASSE}</div>
          <button class="reserverBtn">Réserver</button>
        `;

        card.querySelector('.reserverBtn').addEventListener('click', () => {
          const selectedFlight = {
            id: vol.ID_VOL,
            compagnie: vol.COMPAGNIE,
            numero: vol.NUMERO_VOL,
            origineVille: vol.ORIGINE_VILLE,
            origineCode: vol.ORIGINE_CODE,
            destinationVille: vol.DESTINATION_VILLE,
            destinationCode: vol.DESTINATION_CODE,
            heureDepart: vol.HEURE_DEPART,
            heureArrivee: vol.HEURE_ARRIVEE,
            prix: vol.PRIX,
            classe: vol.CLASSE
          };
          localStorage.setItem('selectedFlight', JSON.stringify(selectedFlight));
          window.location.href = 'confirmation.html';
        });

        resultsCards.appendChild(card);
      });
    } catch (error) {
      loader.style.display = 'none';
      console.error('Erreur recherche vols :', error);
      resultsCards.innerHTML =
        '<p style="text-align:center;color:red;">Erreur serveur.</p>';
    }
  });
});


