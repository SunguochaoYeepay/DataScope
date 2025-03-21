<template>
  <div
    ref="editorContainer"
    class="monaco-editor-container"
    :style="{ height }"
  ></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import loader from '@monaco-editor/loader';
import type * as Monaco from 'monaco-editor';

const props = defineProps<{
  modelValue: string;
  height?: string;
  language?: string;
  theme?: string;
  options?: Monaco.editor.IStandaloneEditorConstructionOptions;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'change', value: string): void;
  (e: 'editor-mounted', editor: Monaco.editor.IStandaloneCodeEditor): void;
}>();

const editorContainer = ref<HTMLElement>();
let editor: Monaco.editor.IStandaloneCodeEditor | null = null;
let monaco: typeof Monaco | null = null;

// 默认配置
const defaultOptions: Monaco.editor.IStandaloneEditorConstructionOptions = {
  theme: 'vs-dark',
  language: 'sql',
  fontSize: 14,
  minimap: {
    enabled: true,
  },
  scrollBeyondLastLine: false,
  automaticLayout: true,
  tabSize: 2,
  wordWrap: 'on',
};

// 初始化编辑器
const initMonaco = async () => {
  if (!editorContainer.value) return;

  try {
    monaco = await loader.init();
    
    // 配置SQL语言
    monaco.languages.registerCompletionItemProvider('sql', {
      provideCompletionItems: () => {
        const suggestions = [
          {
            label: 'SELECT',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'SELECT',
          },
          {
            label: 'FROM',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'FROM',
          },
          {
            label: 'WHERE',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'WHERE',
          },
          {
            label: 'GROUP BY',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'GROUP BY',
          },
          {
            label: 'ORDER BY',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'ORDER BY',
          },
          {
            label: 'LIMIT',
            kind: monaco.languages.CompletionItemKind.Keyword,
            insertText: 'LIMIT',
          },
        ];
        return { suggestions };
      },
    });

    editor = monaco.editor.create(editorContainer.value, {
      value: props.modelValue,
      ...defaultOptions,
      ...props.options,
      theme: props.theme || defaultOptions.theme,
      language: props.language || defaultOptions.language,
    });

    // 监听内容变化
    editor.onDidChangeModelContent(() => {
      const value = editor?.getValue() || '';
      emit('update:modelValue', value);
      emit('change', value);
    });

    // 通知编辑器已挂载
    emit('editor-mounted', editor);
  } catch (error) {
    console.error('Failed to initialize Monaco editor:', error);
  }
};

// 监听值变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (editor && newValue !== editor.getValue()) {
      editor.setValue(newValue);
    }
  }
);

// 监听主题变化
watch(
  () => props.theme,
  (newTheme) => {
    if (monaco && editor && newTheme) {
      monaco.editor.setTheme(newTheme);
    }
  }
);

// 组件挂载时初始化编辑器
onMounted(() => {
  initMonaco();
});

// 组件卸载时销毁编辑器
onBeforeUnmount(() => {
  if (editor) {
    editor.dispose();
  }
});

// 暴露编辑器实例
defineExpose({
  editor: () => editor,
  monaco: () => monaco,
});
</script>

<style scoped>
.monaco-editor-container {
  width: 100%;
  min-height: 200px;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
}
</style>