defmodule ChatlotleWeb.MessageLive.Index do
  use ChatlotleWeb, :live_view

  alias Chatlotle.Channel
  alias Chatlotle.Channel.Message

  @impl true
  def mount(_params, _session, socket) do
    if connected?(socket), do: Channel.subscribe()

    {:ok, stream(socket, :messages, Channel.list_messages())}
  end

  @impl true
  def handle_params(params, _url, socket) do
    {:noreply, apply_action(socket, socket.assigns.live_action, params)}
  end

  defp apply_action(socket, :edit, %{"id" => id}) do
    socket
    |> assign(:page_title, "Edit Message")
    |> assign(:message, Channel.get_message!(id))
  end

  defp apply_action(socket, :new, _params) do
    socket
    |> assign(:page_title, "New Message")
    |> assign(:message, %Message{})
  end

  defp apply_action(socket, :index, _params) do
    socket
    |> assign(:page_title, "Listing Messages")
    |> assign(:message, nil)
  end

  @impl true
  def handle_info({ChatlotleWeb.MessageLive.FormComponent, {:saved, message}}, socket) do
    {:noreply, stream_insert(socket, :messages, message)}
  end

  @impl true
  def handle_inf({:message_created, message}, socket) do
    {:noreply, stream_insert(socket, :messages, message)}
  end


  @impl true
  def handle_event("delete", %{"id" => id}, socket) do
    message = Channel.get_message!(id)
    {:ok, _} = Channel.delete_message(message)

    {:noreply, stream_delete(socket, :messages, message)}
  end
end
