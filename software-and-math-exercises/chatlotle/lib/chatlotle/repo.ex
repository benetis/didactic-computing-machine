defmodule Chatlotle.Repo do
  use Ecto.Repo,
    otp_app: :chatlotle,
    adapter: Ecto.Adapters.Postgres
end
