package model

type Ticket struct {
	ID          string
	Title       string
	Description string
	OwnerID     string
	Priority    int // Assume priority: 1 (low) to 5 (high)
	CreatedAt   int64
}
