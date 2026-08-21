CREATE TABLE optimization_runs (
                                   request_id              UUID PRIMARY KEY,
                                   max_margin              NUMERIC(19, 4) NOT NULL,
                                   total_margin_required   NUMERIC(19, 4) NOT NULL,
                                   total_expected_pnl      NUMERIC(19, 4) NOT NULL,
                                   created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE trades (
                        id                      UUID PRIMARY KEY,
                        optimization_run_id     UUID NOT NULL
                            REFERENCES optimization_runs(request_id) ON DELETE CASCADE,
                        trade_name              VARCHAR(255) NOT NULL,
                        margin_required         NUMERIC(19, 4) NOT NULL,
                        expected_pnl            NUMERIC(19, 4) NOT NULL,
                        selected                BOOLEAN NOT NULL DEFAULT FALSE
);