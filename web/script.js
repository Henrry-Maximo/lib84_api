const CONFIG = {
  // Servidor em produção. Para o Swagger local use 'http://localhost:8080'.
  BASE: 'http://localhost:8080',
  PAGE_SIZE: 9,
  LOOKUP_SIZE: 100,
};

const BTN_GHOST =
  'border border-border bg-surface text-subtle hover:text-text text-xs px-3 py-2 rounded transition-colors disabled:opacity-40';

// Nome da chave que a API usa dentro de "_embedded"
const LIST_KEY = {
  books: 'bookList',
  authors: 'authorList',
  categories: 'categoryList',
  suppliers: 'supplierList',
  users: 'userList',
  loans: 'loanList',
};

const state = {
  resource: 'books',
  page: 0,
  query: '',
  filter: {},
  items: [],
  editing: null,
  onConfirm: null,
  lookups: {}, // listas auxiliares (categorias, autores...) para nomes e selects
  version: localStorage.getItem('lib84_version') || '1',
  token: localStorage.getItem('lib84_token') || '',
};

// ── Helpers ───────────────────────────────────────────────────────

const $ = id => document.getElementById(id);
const isV2 = () => state.version === '2';
const idOf = v => (v && typeof v === 'object' ? v.id : v);
const cut = (s, n = 140) => (s && s.length > n ? s.slice(0, n) + '…' : s);
const esc = v =>
  String(v ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const invalidate = key => { delete state.lookups[key]; };
const findItem = id => state.items.find(i => i.id === id);
const extract = (name, data) => data?._embedded?.[LIST_KEY[name]] ?? [];
const msg = (text, isError = false) =>
  `<p class="${isError ? 'text-red-400' : 'text-muted'} text-xs col-span-full">${esc(text)}</p>`;

const fmtDate = (s, withTime = false) => {
  if (!s) return '—';
  const d = new Date(s.length === 10 ? `${s}T00:00:00` : s);
  if (isNaN(d)) return s;
  return d.toLocaleString('pt-BR', withTime
    ? { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }
    : { day: '2-digit', month: '2-digit', year: 'numeric' });
};

// ── Definição dos recursos ────────────────────────────────────────
// Cada recurso descreve: caminho, campos do formulário, cartão, busca e filtro.

const RES = {
  books: {
    title: 'Livros',
    sub: 'Gerencie o acervo da biblioteca',
    single: 'Livro',
    path: '/books',
    needs: ['categories', 'suppliers', 'authors'],
    label: b => b.title,
    filter: {
      key: 'categoryId',
      source: 'categories',
      placeholder: 'Todas as categorias',
      url: v => `/books/category/${v}`,
    },
    fields: [
      { key: 'title', label: 'Título', required: true },
      { key: 'isbn', label: 'ISBN', required: true },
      { key: 'amount', label: 'Quantidade', type: 'number', required: true },
      { key: 'datePublication', label: 'Data de publicação', type: 'date', required: true },
      { key: 'categoryId', label: 'Categoria', type: 'select', source: 'categories', required: true,
        from: i => idOf(i.category) },
      { key: 'supplierId', label: 'Fornecedor', type: 'select', source: 'suppliers', required: true,
        from: i => idOf(i.supplier) },
      { key: 'authorIds', label: 'Autores', type: 'multiselect', source: 'authors', required: true,
        from: i => (i.authors ?? []).map(idOf) },
    ],
    card: b => ({
      title: b.title,
      rows: [
        ['ISBN', b.isbn],
        ['Quantidade', b.amount],
        ['Categoria', nameOf('categories', idOf(b.category))],
        ['Fornecedor', nameOf('suppliers', idOf(b.supplier))],
        ['Autores', (b.authors ?? []).map(a => a.name).join(', ')],
        ['Publicação', fmtDate(b.datePublication)],
      ],
    }),
    empty: 'Nenhum livro encontrado.',
  },

  authors: {
    title: 'Autores',
    sub: 'Cadastro de autores do acervo',
    single: 'Autor',
    path: '/authors',
    needs: [],
    label: a => a.name,
    search: { path: '/authors/search', param: 'name', label: 'nome' },
    fields: [
      { key: 'name', label: 'Nome', required: true },
      { key: 'biography', label: 'Biografia', type: 'textarea' },
    ],
    card: a => ({ title: a.name, rows: [['Biografia', cut(a.biography)]] }),
    empty: 'Nenhum autor encontrado.',
  },

  categories: {
    title: 'Categorias',
    sub: 'Categorias dos livros',
    single: 'Categoria',
    path: '/categories',
    needs: [],
    label: c => c.title,
    search: { path: '/categories/search', param: 'title', label: 'título' },
    fields: [
      { key: 'title', label: 'Título', required: true },
      { key: 'description', label: 'Descrição', type: 'textarea' },
    ],
    card: c => ({ title: c.title, rows: [['Descrição', cut(c.description)]] }),
    empty: 'Nenhuma categoria encontrada.',
  },

  suppliers: {
    title: 'Fornecedores',
    sub: 'Fornecedores dos livros',
    single: 'Fornecedor',
    path: '/suppliers',
    needs: [],
    label: s => s.name,
    search: { path: '/suppliers/search', param: 'name', label: 'nome' },
    fields: [
      { key: 'name', label: 'Nome', required: true },
      { key: 'email', label: 'E-mail', type: 'email', required: true },
      { key: 'phone', label: 'Telefone' },
      { key: 'cnpj', label: 'CNPJ' },
    ],
    card: s => ({
      title: s.name,
      rows: [['E-mail', s.email], ['Telefone', s.phone], ['CNPJ', s.cnpj]],
    }),
    empty: 'Nenhum fornecedor encontrado.',
  },

  users: {
    title: 'Usuários',
    sub: 'Membros e administradores',
    single: 'Usuário',
    path: '/users',
    needs: [],
    label: u => u.email,
    search: { path: '/users/search', param: 'email', label: 'e-mail' },
    fields: [
      { key: 'email', label: 'E-mail', type: 'email', required: true },
      { key: 'password', label: 'Senha', type: 'password', required: true }, // a API exige senha no PUT também
    ],
    card: u => ({
      title: u.email,
      rows: [['Perfil', u.role], ['Plano', u.plan], ['Criado em', fmtDate(u.createdAt)]],
    }),
    empty: 'Nenhum usuário encontrado.',
  },

  loans: {
    title: 'Empréstimos',
    sub: 'Controle de empréstimos de livros',
    single: 'Empréstimo',
    path: '/loans',
    needs: ['users', 'books'],
    v2Fallback: '/loans/me', // usuário comum na V2 não lista todos os empréstimos
    v2Edit: false,           // a V2 não possui PUT /loans/{id}
    label: l => `Empréstimo · ${l.user?.email ?? ''}`,
    filter: {
      key: 'userId',
      source: 'users',
      placeholder: 'Todos os usuários',
      url: v => `/loans/user/${v}`,
      v1Only: true,
    },
    fields: [
      { key: 'userId', label: 'Usuário', type: 'select', source: 'users', required: true, v1Only: true,
        from: i => idOf(i.user) },
      { key: 'bookIds', label: 'Livros', type: 'multiselect', source: 'books', required: true,
        from: i => (i.books ?? []).map(idOf) },
      { key: 'loanDateScheduled', label: 'Data agendada', type: 'datetime-local', required: true,
        from: i => i.loanDateScheduled?.slice(0, 16) },
    ],
    card: l => ({
      title: l.user?.email ?? 'Empréstimo',
      rows: [
        ['Livros', (l.books ?? []).map(b => b.title).join(', ')],
        ['Agendado', fmtDate(l.loanDateScheduled, true)],
        ['Retirada', fmtDate(l.loanDate, true)],
        ['Devolução', fmtDate(l.loanDateReturn, true)],
      ],
    }),
    empty: 'Nenhum empréstimo encontrado.',
  },
};

// Nome de um item de lookup (ex.: id de categoria -> título)
function nameOf(key, id) {
  const o = state.lookups[key]?.find(x => x.id === id);
  return o ? RES[key].label(o) : '—';
}

// ── Modais e notificações ────────────────────────────────────────

function openModal(name) {
  document.querySelectorAll('.modal').forEach(m => m.classList.add('hidden'));
  $('modal-' + name).classList.remove('hidden');
  $('overlay').classList.remove('hidden');
}

function closeModal() {
  document.querySelectorAll('.modal').forEach(m => m.classList.add('hidden'));
  $('overlay').classList.add('hidden');
}

function toast(text, type = 'ok') {
  const el = document.createElement('div');
  el.className = `text-xs px-4 py-2 rounded border shadow-lg max-w-sm ${
    type === 'error' ? 'bg-red-950 border-red-800 text-red-300' : 'bg-surface border-border text-text'
  }`;
  el.textContent = text;
  $('toasts').appendChild(el);
  setTimeout(() => el.remove(), 4000);
}

function formError(text) {
  const el = $('form-error');
  el.textContent = text;
  el.classList.remove('hidden');
}

// ── API ───────────────────────────────────────────────────────────

async function api(method, path, body, opts = {}) {
  const headers = { 'X-API-Version': state.version };
  if (body) headers['Content-Type'] = 'application/json';

  if (isV2()) {
    if (state.token && !opts.public) headers.Authorization = `Bearer ${state.token}`;
    if (['POST', 'PATCH'].includes(method) && !path.startsWith('/session')) {
      headers['Idempotency-Key'] = crypto.randomUUID();
    }
  }

  let res;
  try {
    res = await fetch(CONFIG.BASE + path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    const err = new Error('Não foi possível conectar à API.');
    err.network = true;
    throw err;
  }

  if (res.status === 401 && !opts.public && state.token) clearSession();
  if (res.status === 204) return null;

  const text = await res.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = { message: text }; }

  if (!res.ok) {
    const err = new Error('api error');
    err.status = res.status;
    err.data = data;
    throw err;
  }
  return data;
}

// Converte erros da API (formato { error } ou { campo: [mensagens] }) em texto
function errMsg(err) {
  if (err.network) return err.message;
  const d = err.data;
  if (d) {
    if (typeof d === 'string') return d;
    if (d.error) return d.error;
    if (d.message) return d.message;
    const fields = Object.entries(d).map(([k, v]) => `${k}: ${[].concat(v).join('; ')}`);
    if (fields.length) return fields.join(' | ');
  }
  if (err.status === 401) return 'Sessão ausente ou expirada. Faça login.';
  if (err.status === 403) return 'Sem permissão para esta ação (requer perfil ADMIN).';
  return err.message || 'Erro inesperado.';
}

// ── Lookups (listas auxiliares, cacheadas) ───────────────────────

async function ensureLookups(keys = []) {
  if (isV2() && !state.token) return;
  await Promise.all(
    keys.filter(k => !state.lookups[k]).map(async k => {
      try {
        state.lookups[k] = extract(k, await api('GET', `/${k}?size=${CONFIG.LOOKUP_SIZE}`));
      } catch {
        state.lookups[k] = [];
      }
    })
  );
}

// ── Renderização de listas ───────────────────────────────────────

async function fetchPage(res) {
  try {
    return await api('GET', `${res.path}?page=${state.page}&size=${CONFIG.PAGE_SIZE}`);
  } catch (e) {
    if (e.status === 403 && res.v2Fallback && isV2()) return api('GET', res.v2Fallback);
    throw e;
  }
}

async function loadList() {
  const res = RES[state.resource];
  const grid = $('grid');
  $('pagination').innerHTML = '';

  if (isV2() && !state.token) {
    grid.innerHTML = msg('Esta versão exige autenticação. Clique em "Entrar" no topo para continuar.');
    return;
  }
  grid.innerHTML = msg('Carregando...');
  await ensureLookups(res.needs);

  const filterValue = res.filter && !(res.filter.v1Only && isV2()) ? state.filter[res.filter.key] : null;
  let items = [];
  let page = null;

  try {
    if (state.query && res.search) {
      const data = await api('GET', `${res.search.path}?${res.search.param}=${encodeURIComponent(state.query)}`);
      items = data ? [data] : [];
    } else if (filterValue) {
      items = extract(state.resource, await api('GET', res.filter.url(filterValue)));
    } else {
      const data = await fetchPage(res);
      items = extract(state.resource, data);
      page = data?.page ?? null;
    }
  } catch (e) {
    if (e.status !== 404) {
      grid.innerHTML = msg('⚠ ' + errMsg(e), true);
      return;
    }
  }

  state.items = items;
  grid.innerHTML = items.length ? items.map(i => cardHTML(i, res)).join('') : msg(res.empty);
  renderPagination(page);
}

function cardHTML(item, res) {
  const c = res.card(item);
  const rows = c.rows
    .map(([k, v]) => `
      <p class="text-xs leading-relaxed">
        <span class="text-muted">${k}:</span>
        <span class="text-text break-words">${v == null || v === '' ? '—' : esc(v)}</span>
      </p>`)
    .join('');
  const canEdit = res.v2Edit !== false || !isV2();

  return `
    <div class="bg-surface border border-border rounded-lg p-4 flex flex-col gap-2 hover:border-blue/50 transition-colors">
      <h3 class="text-white text-sm font-semibold break-words">${esc(c.title)}</h3>
      <div class="flex flex-col gap-1 flex-1">${rows}</div>
      <div class="flex gap-2 mt-2 pt-2 border-t border-border">
        ${canEdit ? `<button data-act="edit" data-id="${item.id}" class="flex-1 text-xs text-subtle hover:text-blue py-1">Editar</button>` : ''}
        <button data-act="del" data-id="${item.id}" class="flex-1 text-xs text-subtle hover:text-red-400 py-1">Excluir</button>
      </div>
    </div>`;
}

function renderPagination(p) {
  const el = $('pagination');
  if (!p) return;
  if (p.totalPages <= 1) {
    el.innerHTML = `<span>${p.totalElements} registro(s)</span>`;
    return;
  }
  el.innerHTML = `
    <button data-act="page" data-page="${p.number - 1}" ${p.number === 0 ? 'disabled' : ''} class="${BTN_GHOST}">← Anterior</button>
    <span>Página ${p.number + 1} de ${p.totalPages} · ${p.totalElements} registros</span>
    <button data-act="page" data-page="${p.number + 1}" ${p.number + 1 >= p.totalPages ? 'disabled' : ''} class="${BTN_GHOST}">Próxima →</button>`;
}

function renderToolbar(res) {
  let html = '';
  if (res.search) {
    html += `
      <input id="q" type="text" value="${esc(state.query)}" placeholder="Buscar por ${res.search.label}..." class="input-field w-64">
      <button data-act="search" class="${BTN_GHOST}">Buscar</button>
      <button data-act="clear" class="text-xs text-muted hover:text-subtle px-2">Limpar</button>`;
  }
  if (res.filter && !(res.filter.v1Only && isV2())) {
    const opts = (state.lookups[res.filter.source] ?? []).map(o => {
      const sel = state.filter[res.filter.key] === o.id ? 'selected' : '';
      return `<option value="${o.id}" ${sel}>${esc(RES[res.filter.source].label(o))}</option>`;
    }).join('');
    html += `
      <select id="flt" class="input-field w-64">
        <option value="">${res.filter.placeholder}</option>${opts}
      </select>`;
  }
  $('toolbar').innerHTML = html;
}

// Recarrega lookups necessários, a barra de ferramentas e a lista
async function refresh() {
  const res = RES[state.resource];
  await ensureLookups(res.needs);
  renderToolbar(res);
  await loadList();
}

async function showResource(name) {
  Object.assign(state, { resource: name, page: 0, query: '', filter: {}, items: [] });
  const res = RES[name];

  document.querySelectorAll('[data-act="tab"]').forEach(b =>
    b.classList.toggle('active', b.dataset.res === name));
  $('view-title').textContent = res.title;
  $('view-subtitle').textContent = res.sub;
  $('btn-new').textContent = `+ Novo ${res.single}`;
  $('btn-new').disabled = isV2() && !state.token;

  await refresh();
}

// ── Formulário (criar / editar) ──────────────────────────────────

function fieldHTML(f, item) {
  const id = `f-${f.key}`;
  const multi = f.type === 'multiselect';
  const raw = item ? (f.from ? f.from(item) : item[f.key]) : (multi ? [] : '');
  const label = `<label for="${id}" class="text-xs text-muted block mb-1">${f.label}${f.required ? ' *' : ''}</label>`;
  let input;

  if (f.type === 'select' || multi) {
    const opts = (state.lookups[f.source] ?? []).map(o => {
      const sel = multi ? (raw ?? []).includes(o.id) : raw === o.id;
      return `<option value="${o.id}" ${sel ? 'selected' : ''}>${esc(RES[f.source].label(o))}</option>`;
    }).join('');
    input = `<select id="${id}" class="input-field" ${multi ? 'multiple size="5"' : ''}>
      ${multi ? '' : '<option value="">Selecione...</option>'}${opts}
    </select>`;
  } else if (f.type === 'textarea') {
    input = `<textarea id="${id}" rows="3" class="input-field resize-none">${esc(raw)}</textarea>`;
  } else {
    input = `<input id="${id}" type="${f.type || 'text'}" value="${esc(raw)}"
      ${f.type === 'number' ? 'min="0"' : ''}
      ${f.type === 'password' ? 'autocomplete="new-password"' : ''}
      class="input-field">`;
  }
  return `<div>${label}${input}</div>`;
}

async function openForm(item) {
  const res = RES[state.resource];
  state.editing = item ?? null;
  await ensureLookups(res.needs);
  $('form-title').textContent = `${item ? 'Editar' : 'Novo'} ${res.single}`;
  $('form-fields').innerHTML = res.fields
    .filter(f => !(f.v1Only && isV2()))
    .map(f => fieldHTML(f, item))
    .join('');
  $('form-error').classList.add('hidden');
  openModal('form');
}

function readForm(res) {
  const body = {};
  for (const f of res.fields) {
    if (f.v1Only && isV2()) continue;
    const el = $('f-' + f.key);
    const v = f.type === 'multiselect'
      ? [...el.selectedOptions].map(o => o.value)
      : (f.type === 'password' ? el.value : el.value.trim());
    const empty = Array.isArray(v) ? v.length === 0 : v === '';
    if (empty) {
      if (f.required) throw new Error(`Preencha o campo "${f.label}".`);
      continue;
    }
    body[f.key] =
      f.type === 'number' ? Number(v) :
      f.type === 'datetime-local' ? `${v}:00` :
      v;
  }
  return body;
}

async function saveForm(e) {
  e.preventDefault();
  const res = RES[state.resource];
  const id = state.editing?.id;
  let body;
  try {
    body = readForm(res);
  } catch (err) {
    return formError(err.message);
  }

  const btn = $('btn-save');
  btn.disabled = true;
  try {
    if (id) await api('PUT', `${res.path}/${id}`, body);
    else await api('POST', res.path, body);
    invalidate(state.resource);
    closeModal();
    toast(`${res.single} ${id ? 'atualizado' : 'criado'} com sucesso.`);
    await refresh();
  } catch (err) {
    formError(errMsg(err));
  } finally {
    btn.disabled = false;
  }
}

// ── Exclusão ─────────────────────────────────────────────────────

function askDelete(item) {
  if (!item) return;
  const res = RES[state.resource];
  $('confirm-text').textContent = `Excluir "${res.label(item)}"? Essa ação não pode ser desfeita.`;
  state.onConfirm = async () => {
    await api('DELETE', `${res.path}/${item.id}`);
    invalidate(state.resource);
    toast(`${res.single} excluído.`);
    await refresh();
  };
  openModal('confirm');
}

// ── Autenticação (V2) ────────────────────────────────────────────

function renderAuth() {
  $('auth-area').innerHTML = !isV2()
    ? ''
    : state.token
      ? `<button data-act="logout" class="${BTN_GHOST}">Sair</button>`
      : `<button data-act="login" class="${BTN_GHOST}">🔑 Entrar</button>`;
}

function clearSession() {
  state.token = '';
  localStorage.removeItem('lib84_token');
  state.lookups = {};
  renderAuth();
}

async function authenticate(register) {
  const email = $('auth-email').value.trim();
  const password = $('auth-password').value;
  const status = $('auth-status');
  if (!email || !password) {
    status.textContent = 'Informe e-mail e senha.';
    return;
  }
  status.textContent = 'Processando...';
  try {
    if (register) await api('POST', '/users', { email, password }, { public: true });
    const data = await api('POST', '/session', { email, password }, { public: true });
    state.token = data.token;
    localStorage.setItem('lib84_token', state.token);
    state.lookups = {};
    $('auth-password').value = '';
    status.textContent = '';
    closeModal();
    renderAuth();
    toast('Sessão iniciada.');
    await showResource(state.resource);
  } catch (e) {
    status.textContent = errMsg(e);
  }
}

function setVersion(v) {
  state.version = v;
  localStorage.setItem('lib84_version', v);
  state.lookups = {};
  renderAuth();
  showResource(state.resource);
}

// ── Eventos ──────────────────────────────────────────────────────

function runSearch() {
  state.query = $('q')?.value.trim() ?? '';
  state.filter = {};
  state.page = 0;
  loadList();
}

document.addEventListener('click', e => {
  const t = e.target.closest('[data-act]');
  if (!t) return;

  switch (t.dataset.act) {
    case 'tab':      return showResource(t.dataset.res);
    case 'new':      return openForm(null);
    case 'edit':     return openForm(findItem(t.dataset.id));
    case 'del':      return askDelete(findItem(t.dataset.id));
    case 'page':     state.page = Number(t.dataset.page); return loadList();
    case 'search':   return runSearch();
    case 'clear':    return showResource(state.resource);
    case 'close':    return closeModal();
    case 'login':    $('auth-status').textContent = ''; return openModal('login');
    case 'logout':   clearSession(); toast('Sessão encerrada.'); return showResource(state.resource);
    case 'do-login': return authenticate(false);
    case 'do-register': return authenticate(true);
  }
});

document.addEventListener('change', e => {
  if (e.target.id !== 'flt') return;
  const key = RES[state.resource].filter.key;
  state.filter = e.target.value ? { [key]: e.target.value } : {};
  state.query = '';
  state.page = 0;
  loadList();
});

document.addEventListener('keydown', e => {
  if (e.key === 'Enter' && e.target.id === 'q') runSearch();
});

document.addEventListener('DOMContentLoaded', () => {
  $('api-version').value = state.version;
  $('api-version').addEventListener('change', e => setVersion(e.target.value));
  $('modal-form').addEventListener('submit', saveForm);
  $('overlay').addEventListener('click', closeModal);
  $('confirm-yes').addEventListener('click', async () => {
    closeModal();
    try {
      await state.onConfirm();
    } catch (err) {
      toast(errMsg(err), 'error');
    }
  });

  renderAuth();
  showResource('books');
});