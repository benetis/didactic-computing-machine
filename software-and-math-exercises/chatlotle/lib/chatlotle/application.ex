defmodule Chatlotle.Application do
  # See https://hexdocs.pm/elixir/Application.html
  # for more information on OTP Applications
  @moduledoc false

  use Application

  @impl true
  def start(_type, _args) do
    children = [
      # Start the Telemetry supervisor
      ChatlotleWeb.Telemetry,
      # Start the Ecto repository
      Chatlotle.Repo,
      # Start the PubSub system
      {Phoenix.PubSub, name: Chatlotle.PubSub},
      # Start Finch
      {Finch, name: Chatlotle.Finch},
      # Start the Endpoint (http/https)
      ChatlotleWeb.Endpoint
      # Start a worker by calling: Chatlotle.Worker.start_link(arg)
      # {Chatlotle.Worker, arg}
    ]

    # See https://hexdocs.pm/elixir/Supervisor.html
    # for other strategies and supported options
    opts = [strategy: :one_for_one, name: Chatlotle.Supervisor]
    Supervisor.start_link(children, opts)
  end

  # Tell Phoenix to update the endpoint configuration
  # whenever the application is updated.
  @impl true
  def config_change(changed, _new, removed) do
    ChatlotleWeb.Endpoint.config_change(changed, removed)
    :ok
  end
end
