<template>
  <div
    ref="editorContainer"
    class="monaco-editor-container"
    :style="{ height }"
  />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue';
import * as monaco from 'monaco-editor';
import { debounce } from 'lodash-es';

interface Props {
  modelValue?: string;
  language?: string;
  theme?: string;
  options?: monaco.editor.IStandaloneEditorConstructionOptions;
  height?: string;
  width?: string;
}

interface Emits {
  (e: 'update:modelValue', value: string): void;
  (e: 'change', value: string): void;
  (e: 'ready', editor: monaco.editor.IStandaloneCodeEditor): void;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  language: 'javascript',
  theme: 'vs',
  height: '300px',
  width: '100%',
});

const emit = defineEmits<Emits>();

const editorContainer = ref<HTMLElement>();
let editor: monaco.editor.IStandaloneCodeEditor;

// 创建编辑器
const createEditor = () => {
  if (!editorContainer.value) return;

  // 基础配置
  const baseOptions: monaco.editor.IStandaloneEditorConstructionOptions = {
    value: props.modelValue,
    language: props.language,
    theme: props.theme,
    automaticLayout: true,
    minimap: { enabled: false },
    scrollBeyondLastLine: false,
    fontSize: 14,
    tabSize: 2,
    wordWrap: 'on',
    lineNumbers: 'on',
    renderWhitespace: 'selection',
    ...props.options,
  };

  // 创建编辑器实例
  editor = monaco.editor.create(editorContainer.value, baseOptions);

  // 监听内容变化
  const debouncedEmit = debounce((value: string) => {
    emit('update:modelValue', value);
    emit('change', value);
  }, 300);

  editor.onDidChangeModelContent(() => {
    const value = editor.getValue();
    debouncedEmit(value);
  });

  // 通知编辑器已就绪
  emit('ready', editor);
};

// 销毁编辑器
const disposeEditor = () => {
  if (editor) {
    editor.dispose();
  }
};

// 更新编辑器内容
const updateContent = () => {
  if (editor) {
    const value = editor.getValue();
    if (value !== props.modelValue) {
      editor.setValue(props.modelValue);
    }
  }
};

// 更新编辑器配置
const updateOptions = () => {
  if (editor) {
    editor.updateOptions(props.options || {});
  }
};

// 监听属性变化
watch(() => props.modelValue, updateContent);
watch(() => props.options, updateOptions, { deep: true });
watch(() => props.language, (newLang) => {
  if (editor) {
    monaco.editor.setModelLanguage(editor.getModel()!, newLang);
  }
});
watch(() => props.theme, (newTheme) => {
  monaco.editor.setTheme(newTheme);
});

// 组件挂载
onMounted(() => {
  createEditor();
});

// 组件卸载
onBeforeUnmount(() => {
  disposeEditor();
});

// 暴露编辑器实例
defineExpose({
  editor: () => editor,
});
</script>

<style lang="scss" scoped>
.monaco-editor-container {
  width: 100%;
  border: 1px solid #d9d9d9;
  border-radius: 2px;
  overflow: hidden;

  :deep(.monaco-editor) {
    .margin {
      background: #f5f5f5;
    }
  }
}
</style>