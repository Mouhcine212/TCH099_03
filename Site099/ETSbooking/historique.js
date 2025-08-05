document.addEventListener('DOMContentLoaded', () => {
  const container = document.getElementById('historyResults');
  const token = localStorage.getItem('token');

  if (!token) {
    window.location.href = 'login.html';
    return;
  }

  function createCard(resa) {
    const card = document.createElement('div');
    card.className = 'history-card';
    card.dataset.id = resa.ID_RESERVATION; 

    const statusColor =
      resa.STATUT === 'Confirmée' ? '#00ff88' :
      resa.STATUT === 'Annulée' ? '#ff4d4d' :
      '#ffcc00';

    const paiementStatus = resa.STATUT_PAIEMENT === 'Payé'
      ? `<span class="paid">Payé</span>`
      : resa.STATUT_PAIEMENT === 'Remboursé'
      ? `<span class="unpaid">Remboursé</span>`
      : `<span class="unpaid">Non payé</span>`;

    card.innerHTML = `
      <h3>${resa.COMPAGNIE} - ${resa.NUMERO_VOL}</h3>
      <p><strong>De :</strong> ${resa.ORIGINE} → <strong>À :</strong> ${resa.DESTINATION}</p>
      <p><strong>Départ :</strong> ${resa.HEURE_DEPART}</p>
      <p><strong>Arrivée :</strong> ${resa.HEURE_ARRIVEE}</p>
      <p><strong>Siège :</strong> ${resa.NUMERO_SIEGE || 'Non attribué'}</p>
      <p class="statut"><strong>Statut :</strong> <span style="color:${statusColor};font-weight:bold">${resa.STATUT}</span></p>
      <p class="paiement"><strong>Paiement :</strong> ${paiementStatus}</p>
    `;

    if (resa.STATUT !== 'Annulée') {
      const cancelBtn = document.createElement('button');
      cancelBtn.textContent = 'Annuler la réservation';
      cancelBtn.className = 'cancel-btn';
      cancelBtn.addEventListener('click', () => annulerReservation(resa.ID_RESERVATION, card));
      card.appendChild(cancelBtn);
    }

    return card;
  }

  function loadHistorique() {
    fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/historique.php', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(data => {
        container.innerHTML = '';

        if (!Array.isArray(data) || data.length === 0) {
          container.innerHTML = '<p style="text-align:center;color:white;">Aucune réservation trouvée.</p>';
          return;
        }

        data.forEach(resa => container.appendChild(createCard(resa)));
      })
      .catch(err => {
        console.error('Erreur chargement historique :', err);
        container.innerHTML = '<p style="color:red;text-align:center;">Erreur serveur.</p>';
      });
  }

  function annulerReservation(idReservation, cardElement) {
    if (!confirm("Voulez-vous vraiment annuler cette réservation ?")) return;

    fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/annuler_reservation.php', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ id_reservation: idReservation })
    })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          alert("Réservation annulée !");

          
          const statutEl = cardElement.querySelector('.statut span');
          statutEl.textContent = data.statut_reservation;
          statutEl.style.color = '#ff4d4d'; 

          const paiementEl = cardElement.querySelector('.paiement');
          paiementEl.innerHTML = `<strong>Paiement :</strong> ${
            data.statut_paiement === 'Remboursé'
              ? '<span class="unpaid">Remboursé</span>'
              : '<span class="unpaid">Non payé</span>'
          }`;

          const cancelBtn = cardElement.querySelector('.cancel-btn');
          if (cancelBtn) cancelBtn.remove();

        } else {
          alert(data.error || "Erreur lors de l'annulation");
        }
      })
      .catch(err => {
        console.error('Erreur annulation :', err);
        alert("Erreur serveur");
      });
  }

  loadHistorique();
});
