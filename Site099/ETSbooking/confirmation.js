document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const detailsContainer = document.getElementById('selectedFlight');
  const confirmBtn = document.querySelector('#confirmationForm button');
  const nomInput = document.getElementById('nomPassager');

  if (!token) {
    window.location.replace('login.html');
    return;
  }

  window.history.pushState(null, '', window.location.href);
  window.addEventListener('popstate', () => {
    window.history.pushState(null, '', window.location.href);
  });

  const selectedFlight = JSON.parse(localStorage.getItem('selectedFlight'));
  if (!selectedFlight) {
    detailsContainer.innerHTML = "<p class='error'>Aucun vol sélectionné.</p>";
    return;
  }

  detailsContainer.innerHTML = `
    <h3 style="color:#ff7a00;">${selectedFlight.compagnie} - ${selectedFlight.numero}</h3>
    <p><strong>${selectedFlight.origineVille} (${selectedFlight.origineCode})</strong> → 
       <strong>${selectedFlight.destinationVille} (${selectedFlight.destinationCode})</strong></p>
    <p>Départ : ${selectedFlight.heureDepart}</p>
    <p>Arrivée : ${selectedFlight.heureArrivee}</p>
    <p>Classe : ${selectedFlight.classe} | Prix : ${selectedFlight.prix} $</p>
  `;

  function parseJwt(token) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }
  const user = parseJwt(token);
  if (user && user.nom) nomInput.value = user.nom;

  confirmBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    const randomRow = Math.floor(Math.random() * 30) + 1;
    const randomSeat = String.fromCharCode(65 + Math.floor(Math.random() * 6));
    const seatNumber = `${randomRow}${randomSeat}`;

    try {
      const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/create_reservation', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          id_vol: selectedFlight.id,
          numero_siege: seatNumber
        })
      });

      const text = await res.text();
      console.log("Réponse brute create_reservation:", text);

      if (!res.ok) {
        alert("Erreur HTTP: " + res.status);
        return;
      }

      let data;
      try {
        data = JSON.parse(text);
      } catch (e) {
        alert("Réponse invalide (non-JSON). Voir console.");
        return;
      }

      if (data.success) {
        const reservationData = {
          id_reservation: data.id_reservation,
          nom: nomInput.value,
          vol: selectedFlight,
          siege: seatNumber
        };
        localStorage.setItem('reservationData', JSON.stringify(reservationData));

        window.location.href = "paiement.html";
      } else {
        alert(data.error || "Erreur lors de la réservation");
      }
    } catch (err) {
      alert("Erreur réseau lors de la réservation.");
    }
  });
});
