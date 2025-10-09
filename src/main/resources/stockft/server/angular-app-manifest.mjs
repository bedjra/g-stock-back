
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "redirectTo": "/login",
    "route": "/"
  },
  {
    "renderMode": 2,
    "route": "/login"
  },
  {
    "renderMode": 2,
    "route": "/dashboard"
  },
  {
    "renderMode": 2,
    "route": "/pdv"
  },
  {
    "renderMode": 2,
    "route": "/reappro"
  },
  {
    "renderMode": 2,
    "route": "/produit"
  },
  {
    "renderMode": 2,
    "route": "/parametre"
  },
  {
    "renderMode": 2,
    "route": "/detail"
  },
  {
    "renderMode": 2,
    "route": "/profil"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 725, hash: '123733fc9d13207189ef6c9155af08ae48bd2c977334cc742a7cde3e7fba993a', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 1057, hash: '98c2cfcca8fce340d880a76c3cb95e8089f1f4d3d16e7c0e3ed3ba58666d0ded', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'login/index.html': {size: 6031, hash: '02fc73aacf7899002cbc824e764da5b64f77b5f4b2825d8203554121f00e579e', text: () => import('./assets-chunks/login_index_html.mjs').then(m => m.default)},
    'pdv/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/pdv_index_html.mjs').then(m => m.default)},
    'produit/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/produit_index_html.mjs').then(m => m.default)},
    'detail/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/detail_index_html.mjs').then(m => m.default)},
    'dashboard/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/dashboard_index_html.mjs').then(m => m.default)},
    'parametre/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/parametre_index_html.mjs').then(m => m.default)},
    'reappro/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/reappro_index_html.mjs').then(m => m.default)},
    'profil/index.html': {size: 14910, hash: 'd1b5b468711f3761b9c7cee3e531d68153c00f982c943a7c01476019b3bd3e94', text: () => import('./assets-chunks/profil_index_html.mjs').then(m => m.default)},
    'styles-6WHQ2B3V.css': {size: 139, hash: 'amnkfZ8M2sE', text: () => import('./assets-chunks/styles-6WHQ2B3V_css.mjs').then(m => m.default)}
  },
};
