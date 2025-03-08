package model

import "time"

type Ticket struct {
	ID          string
	Title       string
	Description string
	OwnerID     string
	Priority    int // Assume priority: 1 (low) to 5 (high)
	CreatedAt   int64
}

func (t *Ticket) IsUrgent() bool {
	return t.Priority >= 4
}

func (t *Ticket) AgeInDays() int {
	return int(time.Since(time.Unix(t.CreatedAt, 0)).Hours() / 24)
}

func (t *Ticket) RequiresAttention() bool {
	return t.IsUrgent() || t.AgeInDays() > 30
}
