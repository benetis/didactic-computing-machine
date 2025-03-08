package factory

import (
	"github.com/google/uuid"
	"property/model"
	"time"
)

func NewTicket(overrides ...func(*model.Ticket)) model.Ticket {
	ticket := model.Ticket{
		ID:          uuid.New().String(),
		Title:       "Test Ticket",
		Description: "This is a sample ticket description.",
		OwnerID:     uuid.New().String(),
		Priority:    3,
		CreatedAt:   time.Now().Unix(),
	}

	for _, override := range overrides {
		override(&ticket)
	}

	return ticket
}
