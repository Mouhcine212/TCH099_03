document.addEventListener('DOMContentLoaded', () => {
  const logoutBtn = document.getElementById('logoutBtn');
  const container = document.getElementById('historyResults');
  const token = localStorage.getItem('token');

  if (!token) {
    window.location.replace('login.html');
    return;
  }

  // Empêche retour arrière
  window.history.pushState(null, '', window.location.href);
  window.addEventListener('popstate', () => {
    window.history.pushState(null, '', window.location.href);
  });

  // Déconnexion
  if (logoutBtn) {
    logoutBtn.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('token');
      window.location.replace('login.html');
    });
  }

  // Chargement de l'historique
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
          const statut = resa.STATUT ?? "Confirmée";
          let statutClass = "confirmed";
          if (statut.toLowerCase().includes('attente')) statutClass = "pending";
          if (statut.toLowerCase().includes('annul')) statutClass = "cancelled";

          const isAnnulee = statutClass === "cancelled";
          const button = isAnnulee 
            ? `<button type="button" class="btn-annuler" disabled>Annulée</button>` 
            : `<button type="button" class="btn-annuler" onclick="annulerReservation(${resa.ID_RESERVATION})">Annuler</button>`;

          return `
            <div class="history-item" id="reservation-${resa.ID_RESERVATION}">
              <div class="history-left">
                <h3>Vol ${resa.ORIGINE} → ${resa.DESTINATION}</h3>
                <p><strong>Départ :</strong> ${new Date(resa.HEURE_DEPART).toLocaleString()}</p>
                <p><strong>Arrivée :</strong> ${new Date(resa.HEURE_ARRIVEE).toLocaleString()}</p>
                <p><strong>Siège :</strong> ${resa.NUMERO_SIEGE || "-"}</p>
              </div>
              <div class="history-right">
                <div>
                  <p><strong>Compagnie :</strong> ${resa.COMPAGNIE}</p>
                  <p><strong>Prix :</strong> ${resa.PRIX} $</p>
                  ${resa.NUMERO_PASSEPORT ? `<p><strong>Passeport :</strong> ${resa.NUMERO_PASSEPORT}</p>` : ''}
                  <span class="statut ${statutClass}">${statut}</span>
                </div>
                ${button}
              </div>
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

// Fonction pour annuler une réservation
async function annulerReservation(reservationId) {
  const token = localStorage.getItem('token');
  if (!token) {
    alert("Vous devez être connecté pour annuler une réservation.");
    window.location.replace('login.html');
    return;
  }

  const payload = { reservation_id: reservationId };
  const url = 'http://localhost/api/annuler_reservation';

  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    const text = await response.text();
    let data;

    try {
      data = JSON.parse(text);
    } catch {
      alert("Erreur serveur : Réponse invalide.");
      console.error("Réponse brute (non JSON):", text);
      return;
    }

    if (data.success) {
      alert(data.message || "Réservation annulée avec succès !");
      
      // Mise à jour dynamique
      const item = document.getElementById(`reservation-${reservationId}`);
      if (item) {
        const statutElem = item.querySelector(".statut");
        const btn = item.querySelector(".btn-annuler");
        if (statutElem) {
          statutElem.textContent = "Annulée";
          statutElem.classList.remove("confirmed", "pending");
          statutElem.classList.add("cancelled");
        }
        if (btn) {
          btn.textContent = "Annulée";
          btn.disabled = true;
        }
      }

    } else {
      alert(data.message || "Impossible d'annuler la réservation.");
    }
  } catch (error) {
    console.error("Erreur réseau :", error);
    alert("Erreur de connexion. Veuillez réessayer.");
  }
}
