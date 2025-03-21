import { App } from 'vue';
import PageContainer from './PageContainer.vue';
import SqlEditor from './SqlEditor.vue';

export default {
  install(app: App) {
    app.component('PageContainer', PageContainer);
    app.component('SqlEditor', SqlEditor);
  },
};