export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user',
        routes: [
          {name: '登录', path: '/user/login', component: './user/Login'},
          {name: '注册', path: '/user/register', component: './user/Register'}
        ],
      },
      {
        component: './404',
      },
    ],
  },
  {
    path: '/welcome',
    name: 'welcome',
    icon: 'smile',
    component: './Welcome',
  },
  {
    path: '/admin',
    name: '管理员',
    icon: 'crown',
    access: 'canAdmin',  // 有访问权限(canAdmin是true时才会允许路由，自动打通了这部分工作)，只有管理员才能访问
    // 'canVIP'
    component: './Admin',
    routes: [  // 添加子路由
      { path: '/admin/user-manage', name: '用户管理', icon: 'simle', component: './Admin/UserManage' },
      {
        component: './404',
      },
    ],
  },
  {
    name: 'list.table-list',
    icon: 'table',
    path: '/list',
    component: './TableList',
  },
  {
    path: '/',
    redirect: '/welcome',
  },
  {
    component: './404',
  },
];
