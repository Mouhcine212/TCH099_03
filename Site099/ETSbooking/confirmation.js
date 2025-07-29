document.addEventListener('DOMContentLoaded', () => {
  const token = localStorage.getItem('token');
  const params = new URLSearchParams(window.location.search);
  const volId = params.get('id');
  const detailsContainer = document.getElementById('confirmation-details');
  const paymentBtn = document.getElementById('paymentBtn');

  if (!token) {
    window.location.replace('login.html');
    return;
  }

  window.history.pushState(null, '', window.location.href);
  window.addEventListener('popstate', () => {
    window.history.pushState(null, '', window.location.href);
  });

  document.getElementById('logoutBtn').addEventListener('click', (e) => {
    e.preventDefault();
    localStorage.removeItem('token');
    window.location.replace('login.html');
  });

  if (!volId) {
    detailsContainer.innerHTML = "<p class='error'>Aucun vol sélectionné.</p>";
    return;
  }

  function parseJwt(token) {
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch (e) {
      return null;
    }
  }

  const user = parseJwt(token);

  fetch(`http://localhost/api/get_flight_by_id/${volId}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
    .then(res => res.json())
    .then(data => {
      if (data.error) {
        detailsContainer.innerHTML = `<p class='error'>${data.error}</p>`;
      } else {
        const vol = data;
        detailsContainer.innerHTML = `
          <div class="card">
            <p><strong>Nom du passager :</strong> ${user.nom}</p>
            <p><strong>Origine :</strong> ${vol.ORIGINE}</p>
            <p><strong>Destination :</strong> ${vol.DESTINATION}</p>
            <p><strong>Date de départ :</strong> ${new Date(vol.HEURE_DEPART).toLocaleString()}</p>
            <p><strong>Heure d’arrivée :</strong> ${new Date(vol.HEURE_ARRIVEE).toLocaleString()}</p>
            <p><strong>Compagnie :</strong> ${vol.COMPAGNIE}</p>
            <p><strong>Numéro de vol :</strong> ${vol.NUMERO_VOL}</p>
            <p><strong>Classe :</strong> ${vol.CLASSE}</p>
            <p><strong>Prix :</strong> ${vol.PRIX} $</p>
          </div>
        `;
      }
    })
    .catch(() => {
      detailsContainer.innerHTML = `<p class='error'>Erreur lors du chargement du vol.</p>`;
    });


  paymentBtn.addEventListener('click', () => {
    sessionStorage.setItem('vol_id', volId); 
    window.location.href = "paiement.html"; 
  });
});
