<template>
  <a-alert
    v-if="error"
    :message="error"
    type="error"
    show-icon
    closable
    @close="onClose"
    style="margin-bottom: 16px"
  />
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';

const props = defineProps<{
  error: string | null;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const onClose = () => {
  emit('close');
};
</script>