CREATE INDEX idx_trades_optimization_run_id
    ON trades (optimization_run_id);

CREATE INDEX idx_optimization_runs_created_at
    ON optimization_runs (created_at DESC);