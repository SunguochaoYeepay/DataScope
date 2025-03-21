<template>
  <div class="page-container">
    <div class="page-header">
      <div class="title-wrapper">
        <h1 class="title">{{ title }}</h1>
        <span v-if="subTitle" class="sub-title">{{ subTitle }}</span>
      </div>
      <div v-if="$slots.extra" class="extra">
        <slot name="extra" />
      </div>
    </div>
    <div class="page-content">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string;
  subTitle?: string;
}>();
</script>

<style lang="scss" scoped>
.page-container {
  background-color: #f0f2f5;
  padding: 16px;
  min-height: 100%;

  .page-header {
    background: #fff;
    padding: 16px 24px;
    border-radius: 2px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .title-wrapper {
      .title {
        margin: 0;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 600;
        font-size: 20px;
        line-height: 32px;
      }

      .sub-title {
        color: rgba(0, 0, 0, 0.45);
        font-size: 14px;
        line-height: 1.5715;
        display: block;
      }
    }

    .extra {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .page-content {
    min-height: calc(100% - 80px);
  }
}
</style>