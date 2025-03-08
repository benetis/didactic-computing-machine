package test

import (
	"github.com/leanovate/gopter"
	"github.com/leanovate/gopter/gen"
	"property/model"
	"time"
)

func TicketGenerator() gopter.Gen {
	return gopter.CombineGens(
		gen.Identifier(), // ID
		gen.AlphaString().SuchThat(func(s string) bool { return len(s) >= 5 }), // Title
		gen.AlphaString().SuchThat(func(s string) bool { return len(s) > 10 }), // Description
		gen.Identifier(),   // ID
		gen.IntRange(1, 5), // Priority
		gen.Int64Range(time.Now().AddDate(-1, 0, 0).Unix(), time.Now().Unix()), // CreatedAt within last year
	).Map(func(vals []interface{}) model.Ticket {
		return model.Ticket{
			ID:          vals[0].(string),
			Title:       vals[1].(string),
			Description: vals[2].(string),
			OwnerID:     vals[3].(string),
			Priority:    vals[4].(int),
			CreatedAt:   vals[5].(int64),
		}
	})
}
