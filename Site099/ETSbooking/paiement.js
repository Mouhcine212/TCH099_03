document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const idVol = sessionStorage.getItem('vol_id');

  if (!token) return window.location.href = 'login.html';
  if (!idVol) return window.location.href = 'index.html';

  const logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('token');
      window.location.replace('login.html');
    });
  }

  const volDetails = document.getElementById('vol-details');
  fetch(`http://localhost/api/get_flight_by_id/${idVol}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(vol => {
      if (vol.error) {
        volDetails.innerHTML = `<p class="error">${vol.error}</p>`;
      } else {
        volDetails.innerHTML = `
          <p><strong>Origine :</strong> ${vol.ORIGINE}</p>
          <p><strong>Destination :</strong> ${vol.DESTINATION}</p>
          <p><strong>Départ :</strong> ${new Date(vol.HEURE_DEPART).toLocaleString()}</p>
          <p><strong>Arrivée :</strong> ${new Date(vol.HEURE_ARRIVEE).toLocaleString()}</p>
          <p><strong>Compagnie :</strong> ${vol.COMPAGNIE}</p>
          <p><strong>Prix :</strong> ${vol.PRIX} $</p>
        `;
      }
    });

  
  const form = document.getElementById('paiementForm');
  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const row = Math.floor(Math.random() * 30) + 1;
    const seat = String.fromCharCode(65 + Math.floor(Math.random() * 6)); // A-F
    const numeroSiege = `${row}${seat}`;

    const dateNaissance = localStorage.getItem('dateNaissance');
    const numeroPasseport = localStorage.getItem('passeport');

    try {
      const res = await fetch('http://localhost/api/paiement', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          id_vol: parseInt(idVol),
          methode: "Carte",
          numero_siege: numeroSiege,
          date_naissance: dateNaissance,
          numero_passeport: numeroPasseport
        })
      });

      const data = await res.json();

      if (res.ok) {
        alert(`Paiement confirmé. Siège : ${numeroSiege}`);
        sessionStorage.removeItem('vol_id');
        window.location.href = "historique.html";
      } else {
        alert(`Paiement échoué : ${data.error || data.details}`);
      }
    } catch (err) {
      alert("Une erreur s’est produite.");
    }
  });
});
