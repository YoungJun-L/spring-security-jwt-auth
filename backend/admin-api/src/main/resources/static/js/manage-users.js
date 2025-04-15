function toggleSidebar() {
    document.getElementById("sidebar").classList.toggle("active");
}

let nextId = null;
let isLoading = false;

function loadUsers() {
    if (isLoading) return;
    isLoading = true;

    fetch(`/users?nextId=${nextId || ""}`)
        .then(response => response.json())
        .then(data => {
            if (data.lastId !== -1) {
                document.getElementById("loadMoreBtn").style.display = "block";
                return;
            }

            const users = data.users;
            const tableBody = document.getElementById("userTableBody");

            users.forEach(user => {
                tableBody.innerHTML += `
                    <tr>
                        <td><label for="check${user.id}"></label><input id="check${user.id}" type="checkbox"></td>
                        <td>${user.email}</td>
                        <td>${user.status}</td>
                        <td>
                            <button class="btn btn-primary btn-sm">Edit</button>
                            <button class="btn btn-danger btn-sm">Delete</button>
                        </td>
                    </tr>
                `;
            });

            if (users.length > 0) {
                nextId = users[users.length - 1].id;
            }
            isLoading = false;
        })
        .catch(error => {
            console.error("Error loading users:", error);
            isLoading = false;
        });
}

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loadMoreBtn").addEventListener("click", loadUsers);
    loadUsers();
});