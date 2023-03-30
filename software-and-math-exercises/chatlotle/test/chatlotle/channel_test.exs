defmodule Chatlotle.ChannelTest do
  use Chatlotle.DataCase

  alias Chatlotle.Channel

  describe "messages" do
    alias Chatlotle.Channel.Message

    import Chatlotle.ChannelFixtures

    @invalid_attrs %{is_read: nil, message: nil, user: nil}

    test "list_messages/0 returns all messages" do
      message = message_fixture()
      assert Channel.list_messages() == [message]
    end

    test "get_message!/1 returns the message with given id" do
      message = message_fixture()
      assert Channel.get_message!(message.id) == message
    end

    test "create_message/1 with valid data creates a message" do
      valid_attrs = %{is_read: true, message: "some message", user: "some user"}

      assert {:ok, %Message{} = message} = Channel.create_message(valid_attrs)
      assert message.is_read == true
      assert message.message == "some message"
      assert message.user == "some user"
    end

    test "create_message/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Channel.create_message(@invalid_attrs)
    end

    test "update_message/2 with valid data updates the message" do
      message = message_fixture()
      update_attrs = %{is_read: false, message: "some updated message", user: "some updated user"}

      assert {:ok, %Message{} = message} = Channel.update_message(message, update_attrs)
      assert message.is_read == false
      assert message.message == "some updated message"
      assert message.user == "some updated user"
    end

    test "update_message/2 with invalid data returns error changeset" do
      message = message_fixture()
      assert {:error, %Ecto.Changeset{}} = Channel.update_message(message, @invalid_attrs)
      assert message == Channel.get_message!(message.id)
    end

    test "delete_message/1 deletes the message" do
      message = message_fixture()
      assert {:ok, %Message{}} = Channel.delete_message(message)
      assert_raise Ecto.NoResultsError, fn -> Channel.get_message!(message.id) end
    end

    test "change_message/1 returns a message changeset" do
      message = message_fixture()
      assert %Ecto.Changeset{} = Channel.change_message(message)
    end
  end
end
