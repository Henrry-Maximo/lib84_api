const BASE = 'http://localhost:8080';
let authToken = localStorage.getItem('lib84_token') || '';

// Inicializa na aba de livros
window.addEventListener('DOMContentLoaded', () => {
  nav('books');
});

// ── Auth & Version ────────────────────────────────────────────────────────────

function changeVersion() {
  const v = document.getElementById('api-version').value;
  console.log('Versão da API alterada para: V' + v);
}

async function doLogin() {
  const email = document.getElementById('auth-email').value;
  const password = document.getElementById('auth-password').value;
  const statusEl = document.getElementById('auth-status');
  statusEl.textContent = 'Autenticando...';
  
  try {
    const res = await fetch(BASE + '/session', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        'X-API-Version': document.getElementById('api-version').value
      },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) throw data;
    
    // Supondo que retorna o token no formato { token: "..." } ou direto a string
    authToken = data.token || data.accessToken || data;
    localStorage.setItem('lib84_token', authToken);
    statusEl.textContent = 'Token JWT obtido com sucesso!';
    setTimeout(() => closeModal(), 1500);
  } catch (e) {
    statusEl.textContent = 'Erro no login: ' + (e.message || JSON.stringify(e));
  }
}

// ── nav ───────────────────────────────────────────────────────────────────────

function nav(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
  document.getElementById(`page-${page}`).classList.add('active');
  document.querySelector(`[data-page="${page}"]`).classList.add('active');
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
  el.textContent = typeof msg === 'object' ? JSON.stringify(msg) : msg;
  el.classList.remove('hidden');
}

function hideError(prefix) {
  const el = document.getElementById(`${prefix}-error`);
  if (el) el.classList.add('hidden');
}

// ── api helper ────────────────────────────────────────────────────────────────

async function api(method, path, body) {
  const version = document.getElementById('api-version').value;
  const headers = { 
    'Content-Type': 'application/json',
    'X-API-Version': version
  };

  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  // Se for POST ou PATCH na V2, injeta o Idempotency-Key automaticamente
  if (version === '2' && (method === 'POST' || method === 'PATCH')) {
    headers['Idempotency-Key'] = crypto.randomUUID();
  }

  const opts = { method, headers };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(BASE + path, opts);
  if (res.status === 204) return null;
  
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  
  if (!res.ok) throw data || { message: res.statusText };
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

function esc(str) {
  return (str || '').replace(/'/g, "\\'");
}

function renderPagination(pageName, pageData, loadFn) {
  const container = document.getElementById(`${pageName}-pagination`);
  if (!pageData) { container.innerHTML = ''; return; }
  container.innerHTML = `
    <span>Página ${pageData.number + 1} de ${pageData.totalPages}</span>
  `;
}

// ── BOOKS ─────────────────────────────────────────────────────────────────────

async function loadBooks(page = 0) {
  const data = await api('GET', `/books?page=${page}&size=9`).catch(() => null);
  const list = document.getElementById('books-list');
  const items = data?._embedded?.bookList ?? (Array.isArray(data) ? data : []);
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
  const data = await api('GET', `/books/category/${q}`).catch(() => null);
  const items = data?._embedded?.bookList ?? (Array.isArray(data) ? data : []);
  document.getElementById('books-list').innerHTML = items.length
    ? items.map(b => card(
        [['Título', b.title], ['ISBN', b.isbn], ['Qtd', b.amount], ['Categoria', b.category?.title]],
        `editBook('${b.id}','${esc(b.title)}','${esc(b.isbn)}',${b.amount},'${b.datePublication}','${b.category?.id}','${b.supplier?.id}','${(b.authors??[]).map(a=>a.id).join(',')}')`,
        `deleteBook('${b.id}')`
      )).join('')
    : empty('Nenhum livro nessa categoria.');
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
    loadBooks();
  } catch (e) {
    showError('book', e);
  }
}

function deleteBook(id) {
  confirmDelete(async () => { await api('DELETE', `/books/${id}`); loadBooks(); });
}

// ── AUTHORS ───────────────────────────────────────────────────────────────────

async function loadAuthors() {
  const data = await api('GET', '/authors?size=50').catch(() => null);
  const items = data?._embedded?.authorList ?? (Array.isArray(data) ? data : []);
  document.getElementById('authors-list').innerHTML = items.length
    ? items.map(a => card(
        [['Nome', a.name], ['Biografia', a.biography]],
        `editAuthor('${a.id}','${esc(a.name)}','${esc(a.biography)}')`,
        `deleteAuthor('${a.id}')`
      )).join('')
    : empty('Nenhum autor encontrado.');
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
  } catch (e) { showError('author', e); }
}

function deleteAuthor(id) {
  confirmDelete(async () => { await api('DELETE', `/authors/${id}`); loadAuthors(); });
}

// ── CATEGORIES ────────────────────────────────────────────────────────────────

async function loadCategories() {
  const data = await api('GET', '/categories?size=50').catch(() => null);
  const items = data?._embedded?.categoryList ?? (Array.isArray(data) ? data : []);
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
  } catch (e) { showError('category', e); }
}

function deleteCategory(id) {
  confirmDelete(async () => { await api('DELETE', `/categories/${id}`); loadCategories(); });
}

// ── SUPPLIERS ─────────────────────────────────────────────────────────────────

async function loadSuppliers() {
  const data = await api('GET', '/suppliers?size=50').catch(() => null);
  const items = data?._embedded?.supplierList ?? (Array.isArray(data) ? data : []);
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
  } catch (e) { showError('supplier', e); }
}

function deleteSupplier(id) {
  confirmDelete(async () => { await api('DELETE', `/suppliers/${id}`); loadSuppliers(); });
}

// ── USERS ─────────────────────────────────────────────────────────────────────

async function loadUsers() {
  const data = await api('GET', '/users').catch(() => null);
  const items = data?._embedded?.userList ?? (Array.isArray(data) ? data : []);
  document.getElementById('users-list').innerHTML = items.length
    ? items.map(u => card(
        [['E-mail', u.email], ['Perfil', u.role], ['Criado em', u.createdAt?.slice(0,10)]],
        `editUser('${u.id}','${esc(u.email)}')`,
        `deleteUser('${u.id}')`
      )).join('')
    : empty('Nenhum usuário encontrado.');
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
  } catch (e) { showError('user', e); }
}

function deleteUser(id) {
  confirmDelete(async () => { await api('DELETE', `/users/${id}`); loadUsers(); });
}

// ── LOANS ─────────────────────────────────────────────────────────────────────

async function loadLoans() {
  const data = await api('GET', '/loans?size=50').catch(() => null);
  const items = data?._embedded?.loanList ?? (Array.isArray(data) ? data : []);
  document.getElementById('loans-list').innerHTML = items.length
    ? items.map(l => card(
        [
          ['Usuário', l.user?.email],
          ['Livros', (l.books ?? []).map(b => b.title).join(', ')],
          ['Emprestado em', l.loanDate?.slice(0,10)]
        ],
        ``,
        `deleteLoan('${l.id}')`
      )).join('')
    : empty('Nenhum empréstimo encontrado.');
}

async function saveLoan() {
  hideError('loan');
  const body = {
    userId: document.getElementById('loan-userId').value.trim(),
    bookIds: document.getElementById('loan-bookIds').value.split(',').map(s => s.trim()).filter(Boolean),
  };
  try {
    await api('POST', '/loans', body);
    closeModal();
    loadLoans();
  } catch (e) { showError('loan', e); }
}

function deleteLoan(id) {
  confirmDelete(async () => { await api('DELETE', `/loans/${id}`); loadLoans(); });
}