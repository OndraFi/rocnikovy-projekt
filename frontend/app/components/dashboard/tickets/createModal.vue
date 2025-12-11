<template>
  <UModal title="Nový ticket">
    <UButton label="Vytvořit ticket" color="primary" variant="solid" />
    <template #body>
      <dashboard-tickets-add-ticket @created="onCreated"/>
    </template>
  </UModal>
</template>
<script lang="ts">
export default {
  methods: {
    onCreated(ticket: tickerResponse) {
      this.$router.push('/dashboard/tickets/' + ticket.id);
    }
  }
}
</script>