document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(window.location.search);
  const idVol = params.get('id'); // doit être ?id_vol=2 dans l'URL
  const flightInfo = document.getElementById('flight-info');
  const seatSelection = document.getElementById('seat-selection');
  const reservationMsg = document.getElementById('reservation-msg');

  if (!idVol) {
    flightInfo.innerHTML = "<p>Vol introuvable.</p>";
    return;
  }

  // Charger les détails du vol
  fetch(`http://localhost:8080/api/vols/${idVol}`)
    .then(res => res.json())
    .then(data => {
      if (data.erreur) {
        flightInfo.innerHTML = `<p>${data.erreur}</p>`;
        return;
      }

      const vol = data;
      flightInfo.innerHTML = `
        <div class="card">
          <h2>Vol ${vol.NUMERO_VOL}</h2>
          <p><strong>Origine :</strong> ${vol.ORIGINE}</p>
          <p><strong>Destination :</strong> ${vol.DESTINATION}</p>
          <p><strong>Départ :</strong> ${new Date(vol.HEURE_DEPART).toLocaleString()}</p>
          <p><strong>Arrivée :</strong> ${new Date(vol.HEURE_ARRIVEE).toLocaleString()}</p>
          <p><strong>Compagnie :</strong> ${vol.COMPAGNIE}</p>
          <p><strong>Classe :</strong> ${vol.CLASSE}</p>
          <p><strong>Prix :</strong> ${vol.PRIX}$</p>
          <p><strong>Sièges disponibles :</strong> ${vol.SIEGES_DISPONIBLES}</p>
        </div>
      `;

      // Générer les sièges (ex: A1 à A10)
      seatSelection.innerHTML = "<h3>Choisissez votre siège :</h3>";
      const seatContainer = document.createElement('div');
      seatContainer.classList.add('seat-container');
      let selectedSeat = null;

      for (let i = 1; i <= vol.SIEGES_DISPONIBLES; i++) {
        const seat = document.createElement('button');
        seat.className = 'seat';
        seat.textContent = `A${i}`;
        seat.addEventListener('click', () => {
          // Sélection unique
          document.querySelectorAll('.seat').forEach(btn => btn.classList.remove('selected'));
          seat.classList.add('selected');
          selectedSeat = seat.textContent;
          reservationMsg.innerHTML = '';
        });
        seatContainer.appendChild(seat);
      }

      seatSelection.appendChild(seatContainer);

      // Ajouter bouton de réservation
      const confirmBtn = document.createElement('button');
      confirmBtn.textContent = "Confirmer la réservation";
      confirmBtn.className = "confirm-btn";
      confirmBtn.addEventListener('click', () => {
        const token = localStorage.getItem('jwt');
        if (!token) {
          alert("Vous devez être connecté pour réserver.");
          return;
        }

        if (!selectedSeat) {
          reservationMsg.innerHTML = "<p style='color:red;'>Veuillez sélectionner un siège.</p>";
          return;
        }

        fetch('http://localhost:8080/api/reservations', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            id_vol: idVol,
            numero_siege: selectedSeat
          })
        })
        .then(res => res.json())
        .then(data => {
          if (data.success) {
            reservationMsg.innerHTML = "<p style='color:green;'>Réservation confirmée!</p>";
          } else {
            reservationMsg.innerHTML = `<p style='color:red;'>${data.error || "Erreur lors de la réservation."}</p>`;
          }
        })
        .catch(() => {
          reservationMsg.innerHTML = "<p style='color:red;'>Erreur réseau.</p>";
        });
      });

      seatSelection.appendChild(confirmBtn);
    })
    .catch(err => {
      flightInfo.innerHTML = "<p>Erreur lors du chargement des données du vol.</p>";
      console.error(err);
    });
});
