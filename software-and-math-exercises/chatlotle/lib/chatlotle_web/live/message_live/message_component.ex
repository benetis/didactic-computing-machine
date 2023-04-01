defmodule ChatlotleWeb.MessageLive.MessageComponent do
  use ChatlotleWeb, :live_component

  def render(assigns) do
    ~H"""
    <div class="message" id={"message-#{@message.id}"}>
      <div class="author"> <%= @message.author %> </div>
      <div class="divider"> | </div>
      <div class="payload"> <%= @message.message %> </div>
      <div class="timestamp"> <%= @message.inserted_at %> </div>
    </div>
    """
  end
end
