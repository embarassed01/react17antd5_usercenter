import { Settings as LayoutSettings } from '@ant-design/pro-layout';
import { join } from 'path';

const Settings: LayoutSettings & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  // 拂晓蓝
  primaryColor: '#1890ff',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: false,
  fixSiderbar: true,
  colorWeak: false,
  title: 'Ant Design Pro',
  pwa: false,
  logo: join(__dirname, '/simpli.jpg'),
  iconfontUrl: '',
};

export default Settings;
