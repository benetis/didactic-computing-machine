defmodule Chatlotle.ChannelFixtures do
  @moduledoc """
  This module defines test helpers for creating
  entities via the `Chatlotle.Channel` context.
  """

  @doc """
  Generate a message.
  """
  def message_fixture(attrs \\ %{}) do
    {:ok, message} =
      attrs
      |> Enum.into(%{
        is_read: true,
        message: "some message",
        user: "some user"
      })
      |> Chatlotle.Channel.create_message()

    message
  end
end
