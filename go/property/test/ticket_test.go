package test

import (
	gc "github.com/leanovate/gopter/convey"
	. "github.com/smartystreets/goconvey/convey"
	"property/model"
	"testing"
)

func TestTicketProperties(t *testing.T) {
	Convey("Ticket property tests", t, func() {
		So(func(ticket model.Ticket) bool {
			age := ticket.AgeInDays()
			return age >= 0 && age <= 365
		}, gc.ShouldSucceedForAll, TicketGenerator())

		So(func(ticket model.Ticket) bool {
			urgent := ticket.IsUrgent()
			return (urgent && ticket.Priority >= 4) || (!urgent && ticket.Priority < 4)
		}, gc.ShouldSucceedForAll, TicketGenerator())

		So(func(ticket model.Ticket) bool {
			needsAttention := ticket.RequiresAttention()
			return needsAttention == (ticket.IsUrgent() || ticket.AgeInDays() > 30)
		}, gc.ShouldSucceedForAll, TicketGenerator())
	})
}
