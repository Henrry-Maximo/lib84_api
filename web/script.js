const BASE = 'http://localhost:8080';

// ── nav ───────────────────────────────────────────────────────────────────────

function nav(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
  document.getElementById(`page-${page}`).classList.add('active');
  document.querySel   ector(`[data-page="${page}"]`).classList.add('active');
  loaders[page]?.();
}

const loaders = {
  books: loadBooks,
  authors: loadAuthors,
  categories: loadCategories,
  suppliers: loadSuppliers,
  users: loadUsers,
  loans: loadLoans,
};

// ── modal ─────────────────────────────────────────────────────────────────────

function openModal(name) {
  document.getElementById('modal-overlay').classList.remove('hidden');
  document.getElementById(`modal-${name}`).classList.remove('hidden');
}

function closeModal() {
  document.getElementById('modal-overlay').classList.add('hidden');
  document.querySelectorAll('[id^="modal-"]').forEach(m => m.classList.add('hidden'));
}

function showError(prefix, msg) {
  const el = document.getElementById(`${prefix}-error`);
  if (!el) return;
  el.textContent = msg;
  el.classList.remove('hidden');
}

function hideError(prefix) {
  const el = document.getElementById(`${prefix}-error`);
  if (el) el.classList.add('hidden');
}

// ── api ───────────────────────────────────────────────────────────────────────

async function api(method, path, body) {
  const opts = { method, headers: { 'Content-Type': 'application/json' } };
  if (body) opts.body = JSON.stringify(body);
  const res = await fetch(BASE + path, opts);
  if (res.status === 204) return null;
  const data = await res.json();
  if (!res.ok) throw data;
  return data;
}

// ── card builder ──────────────────────────────────────────────────────────────

function card(rows, onEdit, onDelete) {
  const fields = rows.map(([label, value]) =>
    `<p class="text-xs leading-relaxed"><span class="text-muted">${label}:</span> <span class="text-text">${value ?? '—'}</span></p>`
  ).join('');
  return `
    <div class="bg-surface border border-border rounded-lg p-4 flex flex-col gap-2 hover:border-blue/50 transition-colors">
      <div class="flex flex-col gap-1 flex-1">${fields}</div>
      <div class="flex gap-2 mt-2 pt-2 border-t border-border">
        <button onclick="${onEdit}" class="flex-1 text-xs text-subtle hover:text-blue transition-colors py-1">Editar</button>
        <button onclick="${onDelete}" class="flex-1 text-xs text-subtle hover:text-red-400 transition-colors py-1">Excluir</button>
      </div>
    </div>`;
}

function empty(msg) {
  return `<p class="text-muted text-xs col-span-3">${msg}</p>`;
}

function confirmDelete(fn) {
  openModal('confirm');
  document.getElementById('confirm-yes').onclick = () => { closeModal(); fn(); };
}

// ── BOOKS ─────────────────────────────────────────────────────────────────────

let booksPage = 0;

async function loadBooks(page = 0) {
  booksPage = page;
  const data = await api('GET', `/books?page=${page}&size=9`).catch(() => null);
  const list = document.getElementById('books-list');
  const items = data?._embedded?.bookList ?? [];
  list.innerHTML = items.length
    ? items.map(b => card(
        [['Título', b.title], ['ISBN', b.isbn], ['Qtd', b.amount], ['Categoria', b.category?.title], ['Publicado', b.datePublication]],
        `editBook('${b.id}','${esc(b.title)}','${esc(b.isbn)}',${b.amount},'${b.datePublication}','${b.category?.id}','${b.supplier?.id}','${(b.authors??[]).map(a=>a.id).join(',')}')`,
        `deleteBook('${b.id}')`
      )).join('')
    : empty('Nenhum livro encontrado.');
  renderPagination('books', data?.page, loadBooks);
}

async function searchByCategory() {
  const q = document.getElementById('search-books').value.trim();
  if (!q) return loadBooks();
  // search category by title first, then get books by categoryId
  const cat = await api('GET', `/categories/search?title=${encodeURIComponent(q)}`).catch(() => null);
  if (!cat?.id) { document.getElementById('books-list').innerHTML = empty('Categoria não encontrada.'); return; }
  const data = await api('GET', `/books/category/${cat.id}`).catch(() => null);
  const items = data?._embedded?.bookList ?? [];
  document.getElementById('books-list').innerHTML = items.length
    ? items.map(b => card(
        [['Título', b.title], ['ISBN', b.isbn], ['Qtd', b.amount], ['Categoria', b.category?.title]],
        `editBook('${b.id}','${esc(b.title)}','${esc(b.isbn)}',${b.amount},'${b.datePublication}','${b.category?.id}','${b.supplier?.id}','${(b.authors??[]).map(a=>a.id).join(',')}')`,
        `deleteBook('${b.id}')`
      )).join('')
    : empty('Nenhum livro nessa categoria.');
  document.getElementById('books-pagination').innerHTML = '';
}

function editBook(id, title, isbn, amount, date, categoryId, supplierId, authorIds) {
  document.getElementById('book-form-title').textContent = 'Editar Livro';
  document.getElementById('book-id').value = id;
  document.getElementById('book-title').value = title;
  document.getElementById('book-isbn').value = isbn;
  document.getElementById('book-amount').value = amount;
  document.getElementById('book-date').value = date;
  document.getElementById('book-categoryId').value = categoryId;
  document.getElementById('book-supplierId').value = supplierId;
  document.getElementById('book-authorIds').value = authorIds;
  hideError('book');
  openModal('book-form');
}

async function saveBook() {
  hideError('book');
  const id = document.getElementById('book-id').value;
  const body = {
    title: document.getElementById('book-title').value.trim(),
    isbn: document.getElementById('book-isbn').value.trim() || undefined,
    amount: parseInt(document.getElementById('book-amount').value) || undefined,
    datePublication: document.getElementById('book-date').value || undefined,
    categoryId: document.getElementById('book-categoryId').value.trim(),
    supplierId: document.getElementById('book-supplierId').value.trim(),
    authorIds: document.getElementById('book-authorIds').value.split(',').map(s => s.trim()).filter(Boolean),
  };
  try {
    id ? await api('PUT', `/books/${id}`, body) : await api('POST', '/books', body);
    closeModal();
    document.getElementById('book-id').value = '';
    document.getElementById('book-form-title').textContent = 'Novo Livro';
    loadBooks(booksPage);
  } catch (e) {
    showError('book', JSON.stringify(e));
  }
}

function deleteBook(id) {
  confirmDelete(async () => { await api('DELETE', `/books/${id}`); loadBooks(booksPage); });
}

// ── AUTHORS ───────────────────────────────────────────────────────────────────

async function loadAuthors() {
  const data = await api('GET', '/authors?size=50').catch(() => null);
  const items = data?._embedded?.authorList ?? [];
  document.getElementById('authors-list').innerHTML = items.length
    ? items.map(a => card(
        [['Nome', a.name], ['Biografia', a.biography]],
        `editAuthor('${a.id}','${esc(a.name)}','${esc(a.biography)}')`,
        `deleteAuthor('${a.id}')`
      )).join('')
    : empty('Nenhum autor encontrado.');
}

async function searchAuthor() {
  const q = document.getElementById('search-authors').value.trim();
  if (!q) return loadAuthors();
  const a = await api('GET', `/authors/search?name=${encodeURIComponent(q)}`).catch(() => null);
  document.getElementById('authors-list').innerHTML = a
    ? card([['Nome', a.name], ['Biografia', a.biography]], `editAuthor('${a.id}','${esc(a.name)}','${esc(a.biography)}')`, `deleteAuthor('${a.id}')`)
    : empty('Autor não encontrado.');
}

function editAuthor(id, name, bio) {
  document.getElementById('author-form-title').textContent = 'Editar Autor';
  document.getElementById('author-id').value = id;
  document.getElementById('author-name').value = name;
  document.getElementById('author-bio').value = bio;
  hideError('author');
  openModal('author-form');
}

async function saveAuthor() {
  hideError('author');
  const id = document.getElementById('author-id').value;
  const body = {
    name: document.getElementById('author-name').value.trim(),
    biography: document.getElementById('author-bio').value.trim() || undefined,
  };
  try {
    id ? await api('PUT', `/authors/${id}`, body) : await api('POST', '/authors', body);
    closeModal();
    document.getElementById('author-id').value = '';
    document.getElementById('author-form-title').textContent = 'Novo Autor';
    loadAuthors();
  } catch (e) { showError('author', JSON.stringify(e)); }
}

function deleteAuthor(id) {
  confirmDelete(async () => { await api('DELETE', `/authors/${id}`); loadAuthors(); });
}

// ── CATEGORIES ────────────────────────────────────────────────────────────────

async function loadCategories() {
  const data = await api('GET', '/categories?size=50').catch(() => null);
  const items = data?._embedded?.categoryList ?? [];
  document.getElementById('categories-list').innerHTML = items.length
    ? items.map(c => card(
        [['Título', c.title], ['Descrição', c.description]],
        `editCategory('${c.id}','${esc(c.title)}','${esc(c.description)}')`,
        `deleteCategory('${c.id}')`
      )).join('')
    : empty('Nenhuma categoria encontrada.');
}

function editCategory(id, title, desc) {
  document.getElementById('category-form-title').textContent = 'Editar Categoria';
  document.getElementById('category-id').value = id;
  document.getElementById('category-title').value = title;
  document.getElementById('category-desc').value = desc;
  hideError('category');
  openModal('category-form');
}

async function saveCategory() {
  hideError('category');
  const id = document.getElementById('category-id').value;
  const body = {
    title: document.getElementById('category-title').value.trim(),
    description: document.getElementById('category-desc').value.trim() || undefined,
  };
  try {
    id ? await api('PUT', `/categories/${id}`, body) : await api('POST', '/categories', body);
    closeModal();
    document.getElementById('category-id').value = '';
    document.getElementById('category-form-title').textContent = 'Nova Categoria';
    loadCategories();
  } catch (e) { showError('category', JSON.stringify(e)); }
}

function deleteCategory(id) {
  confirmDelete(async () => { await api('DELETE', `/categories/${id}`); loadCategories(); });
}

// ── SUPPLIERS ─────────────────────────────────────────────────────────────────

async function loadSuppliers() {
  const data = await api('GET', '/suppliers?size=50').catch(() => null);
  const items = data?._embedded?.supplierList ?? [];
  document.getElementById('suppliers-list').innerHTML = items.length
    ? items.map(s => card(
        [['Nome', s.name], ['E-mail', s.email], ['Telefone', s.phone], ['CNPJ', s.cnpj]],
        `editSupplier('${s.id}','${esc(s.name)}','${esc(s.email)}','${esc(s.phone)}','${esc(s.cnpj)}')`,
        `deleteSupplier('${s.id}')`
      )).join('')
    : empty('Nenhum fornecedor encontrado.');
}

function editSupplier(id, name, email, phone, cnpj) {
  document.getElementById('supplier-form-title').textContent = 'Editar Fornecedor';
  document.getElementById('supplier-id').value = id;
  document.getElementById('supplier-name').value = name;
  document.getElementById('supplier-email').value = email;
  document.getElementById('supplier-phone').value = phone;
  document.getElementById('supplier-cnpj').value = cnpj;
  hideError('supplier');
  openModal('supplier-form');
}

async function saveSupplier() {
  hideError('supplier');
  const id = document.getElementById('supplier-id').value;
  const body = {
    name: document.getElementById('supplier-name').value.trim(),
    email: document.getElementById('supplier-email').value.trim(),
    phone: document.getElementById('supplier-phone').value.trim() || undefined,
    cnpj: document.getElementById('supplier-cnpj').value.trim() || undefined,
  };
  try {
    id ? await api('PUT', `/suppliers/${id}`, body) : await api('POST', '/suppliers', body);
    closeModal();
    document.getElementById('supplier-id').value = '';
    document.getElementById('supplier-form-title').textContent = 'Novo Fornecedor';
    loadSuppliers();
  } catch (e) { showError('supplier', JSON.stringify(e)); }
}

function deleteSupplier(id) {
  confirmDelete(async () => { await api('DELETE', `/suppliers/${id}`); loadSuppliers(); });
}

// ── USERS ─────────────────────────────────────────────────────────────────────

async function loadUsers() {
  const data = await api('GET', '/users?size=50').catch(() => null);
  const items = data?._embedded?.userList ?? [];
  document.getElementById('users-list').innerHTML = items.length
    ? items.map(u => card(
        [['E-mail', u.email], ['Perfil', u.role], ['Criado em', u.createdAt?.slice(0,10)]],
        `editUser('${u.id}','${esc(u.email)}')`,
        `deleteUser('${u.id}')`
      )).join('')
    : empty('Nenhum usuário encontrado.');
}

async function searchUser() {
  const q = document.getElementById('search-users').value.trim();
  if (!q) return loadUsers();
  const u = await api('GET', `/users/search?email=${encodeURIComponent(q)}`).catch(() => null);
  document.getElementById('users-list').innerHTML = u
    ? card([['E-mail', u.email], ['Perfil', u.role], ['Criado em', u.createdAt?.slice(0,10)]], `editUser('${u.id}','${esc(u.email)}')`, `deleteUser('${u.id}')`)
    : empty('Usuário não encontrado.');
}

function editUser(id, email) {
  document.getElementById('user-form-title').textContent = 'Editar Usuário';
  document.getElementById('user-id').value = id;
  document.getElementById('user-email').value = email;
  document.getElementById('user-password').value = '';
  hideError('user');
  openModal('user-form');
}

async function saveUser() {
  hideError('user');
  const id = document.getElementById('user-id').value;
  const body = {
    email: document.getElementById('user-email').value.trim(),
    password: document.getElementById('user-password').value,
  };
  try {
    id ? await api('PUT', `/users/${id}`, body) : await api('POST', '/users', body);
    closeModal();
    document.getElementById('user-id').value = '';
    document.getElementById('user-form-title').textContent = 'Novo Usuário';
    loadUsers();
  } catch (e) { showError('user', JSON.stringify(e)); }
}

function deleteUser(id) {
  confirmDelete(async () => { await api('DELETE', `/users/${id}`); loadUsers(); });
}

// ── LOANS ─────────────────────────────────────────────────────────────────────

async function loadLoans() {
  const data = await api('GET', '/loans?size=50').catch(() => null);
  const items = data?._embedded?.loanList ?? [];
  document.getElementById('loans-list').innerHTML = items.length
    ? items.map(l => card(
        [
          ['Usuário', l.user?.email],
          ['Livros', (l.books ?? []).map(b => b.title).join(', ')],
          ['Emprestado em', l.loanDate?.slice(0,10)],
          ['Previsto', l.loanDateScheduled?.slice(0,10)],
          ['Devolvido', l.loanDateReturn?.slice(0,10)],
        ],
        `editLoan('${l.id}','${l.user?.id}','${(l.books??[]).map(b=>b.id).join(',')}')`,
        `deleteLoan('${l.id}')`
      )).join('')
    : empty('Nenhum empréstimo encontrado.');
}

function editLoan(id, userId, bookIds) {
  document.getElementById('loan-userId').value = userId;
  document.getElementById('loan-bookIds').value = bookIds;
  document.getElementById('loan-date').value = '';
  hideError('loan');
  openModal('loan-form');
  document.querySelector('#modal-loan-form button[onclick="saveLoan()"]').onclick = () => saveLoan(id);
}

async function saveLoan(id) {
  hideError('loan');
  const body = {
    userId: document.getElementById('loan-userId').value.trim(),
    bookIds: document.getElementById('loan-bookIds').value.split(',').map(s => s.trim()).filter(Boolean),
    loanDateScheduled: document.getElementById('loan-date').value || undefined,
  };
  try {
    id ? await api('PUT', `/loans/${id}`, body) : await api('POST', '/loans', body);
    closeModal();
    loadLoans();
  } catch (e) { showError('loan', JSON.stringify(e)); }
}

function deleteLoan(id) {
  confirmDelete(async () => { await api('DELETE', `/loans/${id}`); loadLoans(); });
}

// ── pagination ────────────────────────────────────────────────────────────────

function renderPagination(prefix, page, loader) {
  const el = document.getElementById(`${prefix}-pagination`);
  if (!el || !page || page.totalPages <= 1) { if (el) el.innerHTML = ''; return; }
  const { number, totalPages } = page;
  let html = '';
  if (number > 0) html += `<button onclick="${loader.name}(${number - 1})" class="px-3 py-1 border border-border rounded hover:border-blue text-subtle hover:text-text transition-colors">← Anterior</button>`;
  html += `<span class="text-muted px-2">${number + 1} / ${totalPages}</span>`;
  if (number < totalPages - 1) html += `<button onclick="${loader.name}(${number + 1})" class="px-3 py-1 border border-border rounded hover:border-blue text-subtle hover:text-text transition-colors">Próxima →</button>`;
  el.innerHTML = html;
}

// ── utils ─────────────────────────────────────────────────────────────────────

function esc(s) { return (s ?? '').replace(/'/g, "\\'").replace(/"/g, '&quot;'); }

// ── init ──────────────────────────────────────────────────────────────────────

nav('books');
