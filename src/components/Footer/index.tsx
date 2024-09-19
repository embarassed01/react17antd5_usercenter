import { DefaultFooter } from '@ant-design/pro-layout';

const Footer: React.FC = () => {
  const defaultMessage = '鱼皮出品';
  const currentYear = new Date().getFullYear();

  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'planet',
          title: '知识星球',
          href: '',
          blankTarget: true,
        },
        {
          key: 'codeNav',
          title: '编程导航',
          href: '',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
