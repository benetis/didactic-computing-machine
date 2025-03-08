package test

import (
	gc "github.com/leanovate/gopter/convey"
	. "github.com/smartystreets/goconvey/convey"
	"property/model"
	"testing"
	"time"
)

func TestTicketProperties(t *testing.T) {
	Convey("Ticket property tests", t, func() {
		So(func(ticket model.Ticket) bool {
			now := time.Now().Unix()
			oneYearAgo := time.Now().AddDate(-1, 0, 0).Unix()
			return ticket.CreatedAt >= oneYearAgo && ticket.CreatedAt <= now
		}, gc.ShouldSucceedForAll, TicketGenerator())

		So(func(ticket model.Ticket) bool {
			return ticket.Priority >= 1 && ticket.Priority <= 5
		}, gc.ShouldSucceedForAll, TicketGenerator())

		So(func(ticket model.Ticket) bool {
			return len(ticket.ID) > 0 &&
				len(ticket.Title) >= 5 &&
				len(ticket.Description) > 10 &&
				len(ticket.OwnerID) > 0
		}, gc.ShouldSucceedForAll, TicketGenerator())
	})
}
