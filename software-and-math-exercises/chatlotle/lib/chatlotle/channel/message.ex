defmodule Chatlotle.Channel.Message do
  use Ecto.Schema
  import Ecto.Changeset

  schema "messages" do
    field :is_read, :boolean, default: false
    field :message, :string
    field :user, :string

    timestamps()
  end

  @doc false
  def changeset(message, attrs) do
    message
    |> cast(attrs, [:user, :message, :is_read])
    |> validate_required([:user, :message, :is_read])
  end
end
