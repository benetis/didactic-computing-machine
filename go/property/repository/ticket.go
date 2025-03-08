package repository

import (
	"database/sql"
	_ "github.com/mattn/go-sqlite3"
	"property/model"
)

type TicketRepo struct {
	DB *sql.DB
}

func NewTicketRepo(db *sql.DB) *TicketRepo {
	return &TicketRepo{DB: db}
}

func (r *TicketRepo) Init() error {
	query := `
	CREATE TABLE IF NOT EXISTS tickets (
		id TEXT PRIMARY KEY,
		title TEXT,
		description TEXT,
		owner_id TEXT,
		priority INTEGER,
		created_at INTEGER
	)`
	_, err := r.DB.Exec(query)
	return err
}

func (r *TicketRepo) Save(ticket model.Ticket) error {
	_, err := r.DB.Exec(
		"INSERT INTO tickets (id, title, description, owner_id, priority, created_at) VALUES (?, ?, ?, ?, ?, ?)",
		ticket.ID, ticket.Title, ticket.Description, ticket.OwnerID, ticket.Priority, ticket.CreatedAt,
	)
	return err
}

func (r *TicketRepo) GetByID(id string) (*model.Ticket, error) {
	row := r.DB.QueryRow("SELECT id, title, description, owner_id, priority, created_at FROM tickets WHERE id = ?", id)
	var t model.Ticket
	err := row.Scan(&t.ID, &t.Title, &t.Description, &t.OwnerID, &t.Priority, &t.CreatedAt)
	if err != nil {
		return nil, err
	}
	return &t, nil
}
