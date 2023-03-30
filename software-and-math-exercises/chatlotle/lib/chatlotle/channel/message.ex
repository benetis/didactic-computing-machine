defmodule Chatlotle.Channel.Message do
  use Ecto.Schema
  import Ecto.Changeset

  schema "messages" do
    field :is_read, :boolean, default: false
    field :message, :string
    field :user, :string, default: "admin"

    timestamps()
  end

  @doc false
  def changeset(message, attrs) do
    message
    |> cast(attrs, [:message])
    |> validate_required([:message])
    |> validate_length(:message, min: 1, max: 200)
  end
end
