document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('paiementForm');
  const cardNumber = document.getElementById('numCarte');
  const expiryDate = document.getElementById('expCarte');
  const cvv = document.getElementById('cvvCarte');

  const token = localStorage.getItem('token');
  const reservationData = JSON.parse(localStorage.getItem('reservationData'));

  if (!token || !reservationData) {
    alert("Aucune réservation à payer !");
    window.location.href = "recherche.html";
    return;
  }

  console.log('Debug paiement:', reservationData);

  cardNumber.addEventListener('input', () => {
    let val = cardNumber.value.replace(/\D/g, '');
    cardNumber.value = val.replace(/(.{4})/g, '$1 ').trim();
  });

  expiryDate.addEventListener('input', () => {
    let val = expiryDate.value.replace(/\D/g, '');
    if (val.length >= 3) {
      expiryDate.value = val.slice(0, 2) + '/' + val.slice(2, 4);
    } else {
      expiryDate.value = val;
    }
  });

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const num = cardNumber.value.replace(/\s/g, '');
    const exp = expiryDate.value;
    const cvvVal = cvv.value;

    if (!/^\d{16}$/.test(num)) {
      alert("Numéro de carte invalide (16 chiffres)");
      return;
    }
    if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(exp)) {
      alert("Date d'expiration invalide (MM/AA)");
      return;
    }
    if (!/^\d{3}$/.test(cvvVal)) {
      alert("CVV invalide (3 chiffres)");
      return;
    }

    const idReservation = reservationData.id_reservation;
    const montant = reservationData.vol.PRIX || reservationData.vol.prix;

    if (!idReservation || !montant) {
      alert("Impossible de procéder au paiement : réservation incomplète.");
      console.error('reservationData incomplet:', reservationData);
      return;
    }

    try {
      const res = await fetch('https://flightets-gghremf5czh9d3ea.canadacentral-01.azurewebsites.net/api/endpoints/process_payment.php', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          id_reservation: idReservation,
          montant: montant,
          methode: 'Carte'
        })
      });

      const data = await res.json();
      console.log('Réponse paiement:', data);

      if (data.success) {
        alert('Paiement réussi !');
        localStorage.removeItem('reservationData');
        window.location.href = "historique.html";
      } else {
        alert(data.error || 'Erreur lors du paiement.');
      }

    } catch (error) {
      alert("Erreur réseau lors du paiement.");
      console.error(error);
    }
  });
});

