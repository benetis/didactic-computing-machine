defmodule Chatlotle.Repo.Migrations.CreateMessages do
  use Ecto.Migration

  def change do
    create table(:messages) do
      add :user, :string
      add :message, :string
      add :is_read, :boolean, default: false, null: false

      timestamps()
    end
  end
end
